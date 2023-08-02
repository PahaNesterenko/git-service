package com.example.gitservice.integration;

import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.dto.CommitDTO;
import com.example.gitservice.dto.OwnerDTO;
import com.example.gitservice.dto.RepositoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class GitServiceIntegrationTest {

    private static WireMockServer wireMockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("external.resource.github", wireMockServer::baseUrl);
    }

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @AfterEach
    void reset() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldReturnReposSuccessfully() throws Exception {

        String responseBody = objectMapper.writeValueAsString(List.of(new RepositoryDTO("name", false, new OwnerDTO("login"))));

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        String responseBodyBranch = objectMapper.writeValueAsString(List.of(new BranchDTO("master", new CommitDTO("ffdg"))));

        wireMockServer.stubFor(get(urlPathMatching("/repos/([A-Za-z]+)/([A-Za-z]+)/branches.*"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyBranch)));

        ResultActions result = executeGetRequest();

        result.andExpect(status().isOk()).andExpect(content().json("[{\"name\":\"name\",\"ownerLogin\":\"login\",\"branches\":[{\"name\":\"master\",\"lastCommitSha\":\"ffdg\"}]}]"));

        wireMockServer.verify(getRequestedFor(urlEqualTo("/users/user/repos?per_page=100&page=1")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/repos/user/name/branches")));
    }

    @Test
    void shouldReturnNotFound() throws Exception {

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(notFound()));

        ResultActions result = executeGetRequest();

        result.andExpect(status().isNotFound()).andExpect(content().json("{\"status\":404,\"message\":\"Resource not found\"}"));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/users/user/repos?per_page=100&page=1")));
    }

    @Test
    void shouldMakeAdditionalCallIfReposMoreThen100() throws Exception {
        String responseBody = objectMapper.writeValueAsString(Collections.nCopies(100, new RepositoryDTO("name", false, new OwnerDTO("login"))));
        String responseBodySecondCall = objectMapper.writeValueAsString(Collections.nCopies(5, new RepositoryDTO("name", false, new OwnerDTO("login"))));

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("2"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodySecondCall)));

        String responseBodyBranch = objectMapper.writeValueAsString(List.of(new BranchDTO("master", new CommitDTO("ffdg"))));

        wireMockServer.stubFor(get(urlPathMatching("/repos/([A-Za-z]+)/([A-Za-z]+)/branches.*"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyBranch)));

        ResultActions result = executeGetRequest();

        result.andExpect(status().isOk()).andExpect(jsonPath("$.*", hasSize(105)));

        wireMockServer.verify(getRequestedFor(urlEqualTo("/users/user/repos?per_page=100&page=1")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/users/user/repos?per_page=100&page=2")));
        wireMockServer.verify(105, getRequestedFor(urlEqualTo("/repos/user/name/branches")));
    }

    private ResultActions executeGetRequest() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/repos").queryParam("username", "user"));
    }
}
