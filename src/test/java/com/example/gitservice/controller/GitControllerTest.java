package com.example.gitservice.controller;

import com.example.gitservice.exception.ResourceNotFoundException;
import com.example.gitservice.model.BranchModel;
import com.example.gitservice.model.RepositoryModel;
import com.example.gitservice.service.GitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GitController.class)
@AutoConfigureMockMvc
public class GitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitService gitService;

    @Test
    public void shouldReturnRepositories() throws Exception {
        RepositoryModel repo = new RepositoryModel("repo1", "owner", List.of(new BranchModel("name", "fgh4sf")));
        when(gitService.findRepositoriesForUser("user")).thenReturn(List.of(repo));

        mockMvc.perform(get("/repos").param("username", "user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("repo1")))
                .andExpect(jsonPath("$[0].ownerLogin", is("owner")))
                .andExpect(jsonPath("$[0].branches.*", hasSize(1)))
                .andExpect(jsonPath("$[0].branches[0].name", is("name")))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha", is("fgh4sf")));
    }

    @Test
    public void shouldReturnErrorIfUserNotFound() throws Exception {
        when(gitService.findRepositoriesForUser("user")).thenThrow(new ResourceNotFoundException(404, "Not found"));

        mockMvc.perform(get("/repos").param("username", "user"))
                .andExpect(status().isNotFound());
    }
}
