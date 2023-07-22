package com.example.gitservice.service;

import com.example.gitservice.model.RepositoryModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitService {

    private final VendorGitService vendorGitService;

    public GitService(VendorGitService vendorGitService) {
        this.vendorGitService = vendorGitService;
    }

    public List<RepositoryModel> findRepositoriesForUser(String username) {
        return vendorGitService.findRepositoriesForUser(username);
    }
}
