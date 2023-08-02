package com.example.gitservice.model;

import java.util.List;

public record RepositoryModel(String name, String ownerLogin, List<BranchModel> branches) {
}
