package com.example.gitservice.service.github;

import com.example.gitservice.client.GitHubHttpClient;
import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.model.BranchModel;
import com.example.gitservice.model.RepositoryModel;
import com.example.gitservice.service.mapper.BranchMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GitHubBranchService {

    private final GitHubHttpClient client;
    private final BranchMapper branchMapper;

    public GitHubBranchService(GitHubHttpClient client, BranchMapper branchMapper) {
        this.client = client;
        this.branchMapper = branchMapper;
    }

    @Async
    public CompletableFuture<RepositoryModel> populateBranches(String username, RepositoryModel repo) {
        List<BranchDTO> branchDTOS = client.getBranches(username, repo.getName());
        List<BranchModel> branches = branchDTOS.stream().map(branchMapper::convertBranch).toList();
        repo.setBranches(branches);
        return CompletableFuture.completedFuture(repo);
    }

}
