package com.example.gitservice.service;

import com.example.gitservice.model.RepositoryModel;

import java.util.List;

public interface VendorGitService {

    List<RepositoryModel> findRepositoriesForUser(String username);

}
