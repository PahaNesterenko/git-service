package com.example.gitservice.integration;

import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.dto.CommitDTO;
import com.example.gitservice.dto.OwnerDTO;
import com.example.gitservice.dto.RepositoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class GitServiceIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private WireMockServer wireMockServer;

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("external.resource.github", () -> "http://localhost:8888");
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(options().port(8888));
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnReposSuccessfully() throws Exception {
        RepositoryDTO repositoryDTO = new RepositoryDTO("name", false, new OwnerDTO("login"));
        String responseBody = objectMapper.writeValueAsString(Arrays.asList(repositoryDTO));

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        BranchDTO branchDTO = new BranchDTO("master", new CommitDTO("ffdg"));
        String responseBodyBranch = objectMapper.writeValueAsString(Arrays.asList(branchDTO));

        wireMockServer.stubFor(get(urlPathMatching("/repos/([A-Za-z]+)/([A-Za-z]+)/branches.*"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyBranch)));

        ResultActions result = executeGetRequest();

        result.andExpect(status().isOk()).andExpect(content().json("[{\"name\":\"name\",\"ownerLogin\":\"login\",\"branches\":[{\"name\":\"master\",\"lastCommitSha\":\"ffdg\"}]}]"));
    }

    @Test
    void shouldReturnNotFound() throws Exception {

        wireMockServer.stubFor(get(urlPathMatching("/users/([A-Za-z]+)/repos.*"))
                .withQueryParam("per_page", equalTo("100"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(notFound()));

        ResultActions result = executeGetRequest();

        result.andExpect(status().isNotFound()).andExpect(content().json("{\"status\":404,\"message\":\"Resource not found\"}"));
    }

    private ResultActions executeGetRequest() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/repos").queryParam("username", "user"));
    }
}
