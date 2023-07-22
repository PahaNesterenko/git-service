package com.example.gitservice.client;

import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.dto.RepositoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "github-api", url = "${external.resource.github}")
public interface GitHubHttpClient {

    @GetMapping("/users/{username}/repos")
    List<RepositoryDTO> getRepositoriesForUser(@PathVariable("username") String userName,
                                               @RequestParam("per_page") int perPage,
                                               @RequestParam("page") int page);

    @GetMapping("repos/{username}/{repo}/branches")
    List<BranchDTO> getBranches(@PathVariable("username") String userName, @PathVariable("repo") String repo);

}

