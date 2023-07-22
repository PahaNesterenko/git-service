package com.example.gitservice.controller;

import com.example.gitservice.model.RepositoryModel;
import com.example.gitservice.service.GitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GitController {

    private final GitService gitService;

    public GitController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping(value = "/repos", produces = "application/json")
    public List<RepositoryModel> getRepositoriesForUser(@RequestParam String username) {
        return gitService.findRepositoriesForUser(username);
    }

}
