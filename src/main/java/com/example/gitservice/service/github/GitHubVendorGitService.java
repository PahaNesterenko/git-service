package com.example.gitservice.service.github;

import com.example.gitservice.client.GitHubHttpClient;
import com.example.gitservice.dto.RepositoryDTO;
import com.example.gitservice.model.RepositoryModel;
import com.example.gitservice.service.VendorGitService;
import com.example.gitservice.service.mapper.RepositoryMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GitHubVendorGitService implements VendorGitService {

    private static final Integer MAX_NUMBER_OF_REPOS_PER_PAGE = 100;
    private final GitHubHttpClient client;
    private final GitHubBranchService branchService;
    private final RepositoryMapper repositoryMapper;

    public GitHubVendorGitService(GitHubHttpClient client, GitHubBranchService branchService, RepositoryMapper repositoryMapper) {
        this.client = client;
        this.branchService = branchService;
        this.repositoryMapper = repositoryMapper;
    }

    @Override
    public List<RepositoryModel> findRepositoriesForUser(String username) {
        int pageNumber = 1;
        List<RepositoryDTO> githubRepos = new ArrayList<>();
        githubRepos.addAll(client.getRepositoriesForUser(username, MAX_NUMBER_OF_REPOS_PER_PAGE, pageNumber));
        while (!githubRepos.isEmpty() && githubRepos.size() % MAX_NUMBER_OF_REPOS_PER_PAGE == 0) {
            githubRepos.addAll(client.getRepositoriesForUser(username, MAX_NUMBER_OF_REPOS_PER_PAGE, ++pageNumber));
        }
        githubRepos.removeIf(RepositoryDTO::isFork);
        List<RepositoryModel> repos = githubRepos.stream().map(repositoryMapper::convertRepository).toList();
        List<CompletableFuture<RepositoryModel>> list = repos.stream()
                .map(repo -> branchService.populateBranches(username, repo)).toList();

        return list.stream().map(CompletableFuture::join).toList();
    }
}
