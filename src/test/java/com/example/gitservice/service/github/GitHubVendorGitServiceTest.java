package com.example.gitservice.service.github;

import com.example.gitservice.client.GitHubHttpClient;
import com.example.gitservice.dto.OwnerDTO;
import com.example.gitservice.dto.RepositoryDTO;
import com.example.gitservice.model.BranchModel;
import com.example.gitservice.model.RepositoryModel;
import com.example.gitservice.service.mapper.RepositoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubVendorGitServiceTest {

    @Mock
    private GitHubHttpClient client;
    @Mock
    private GitHubBranchService branchService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private RepositoryMapper repositoryMapper;

    @InjectMocks
    private GitHubVendorGitService service;

    private static final RepositoryDTO repositoryDTO = new RepositoryDTO("repo", false, new OwnerDTO("login"));
    private static final RepositoryModel repositoryModel = new RepositoryModel("repo", "login", Arrays.asList(new BranchModel("master", "sha")));

    @Test
    public void shouldReturnRepositories(){
        when(client.getRepositoriesForUser(anyString(), anyInt(), anyInt())).thenReturn(Arrays.asList(repositoryDTO));
        when(branchService.populateBranches(anyString(), any(RepositoryModel.class))).thenReturn(CompletableFuture.completedFuture(repositoryModel));

        List<RepositoryModel> result = service.findRepositoriesForUser("user");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("repo", result.get(0).getName());
    }

    @Test
    public void shouldReturnEmptyIfNoRepos(){
        when(client.getRepositoriesForUser(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<>());

        List<RepositoryModel> result = service.findRepositoriesForUser("user");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFilterForkRepositories(){
        when(client.getRepositoriesForUser(anyString(), anyInt(), anyInt())).thenReturn(List.of(repositoryDTO,
                new RepositoryDTO("repo", true, new OwnerDTO("login"))));
        when(branchService.populateBranches(anyString(), any(RepositoryModel.class))).thenReturn(CompletableFuture.completedFuture(repositoryModel));

        List<RepositoryModel> result = service.findRepositoriesForUser("user");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("repo", result.get(0).getName());
    }

    @Test
    public void shouldMakeAdditionalCallIfThereAreMoreRepositories(){

        when(client.getRepositoriesForUser(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.nCopies(100, repositoryDTO))
                .thenReturn(Collections.nCopies(5, repositoryDTO));
        when(branchService.populateBranches(anyString(), any(RepositoryModel.class))).thenReturn(CompletableFuture.completedFuture(repositoryModel));
        List<RepositoryModel> result = service.findRepositoriesForUser("user");

        assertNotNull(result);
        assertEquals(105, result.size());
        assertEquals("repo", result.get(0).getName());
        verify(client, times(2)).getRepositoriesForUser(anyString(), anyInt(), anyInt());
    }
}
