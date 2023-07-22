package com.example.gitservice.integration;

import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.dto.CommitDTO;
import com.example.gitservice.dto.OwnerDTO;
import com.example.gitservice.dto.RepositoryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
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

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class GitServiceIntegrationTest {

    public static MockWebServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @DynamicPropertySource
    static void backendProperties(DynamicPropertyRegistry registry) {
        registry.add("external.resource.github", () -> mockServer.url("/").toString());
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void shouldReturnReposSuccessfully() throws Exception {
        RepositoryDTO repositoryDTO = new RepositoryDTO("name", false, new OwnerDTO("login"));
        String responseBody = objectMapper.writeValueAsString(Arrays.asList(repositoryDTO));
        mockExternalEndpoint(200, responseBody);

        BranchDTO branchDTO = new BranchDTO("master", new CommitDTO("ffdg"));
        String responseBodyBranch = objectMapper.writeValueAsString(Arrays.asList(branchDTO));
        mockExternalEndpoint(200, responseBodyBranch);

        ResultActions result = executeGetRequest();

        assertBackendServerWasCalledCorrectlyForGET(mockServer.takeRequest());
        verifyResults(result, 200, "{\"name\":\"name\",\"ownerLogin\":\"login\",\"branches\":[{\"name\":\"master\",\"lastCommitSha\":\"ffdg\"}]}");
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        mockExternalEndpoint(404, "not found");
        ResultActions result = executeGetRequest();

        assertBackendServerWasCalledCorrectlyForGET(mockServer.takeRequest());
        verifyResults(result, 404, "{\"status\":404,\"message\":\"Resource not found\"}");
    }

    private ResultActions executeGetRequest() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/repos").queryParam("username", "user"));
    }

    private void mockExternalEndpoint(int responseCode, String body) {
        MockResponse mockResponse = new MockResponse().setResponseCode(responseCode)
                .setBody(body)
                .addHeader("Content-Type", "application/json");
        mockServer.enqueue(mockResponse);
    }

    private void assertBackendServerWasCalledCorrectlyForGET(RecordedRequest recordedRequest) {
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/users/user/repos?per_page=100&page=1");
    }

    private void verifyResults(ResultActions resultActions, int status, String... message) throws Exception {
        resultActions
                .andDo(print())
                .andExpect(status().is(status));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(responseBody).contains(message);
    }
}
