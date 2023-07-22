package com.example.gitservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryModel {
    private String name;
    private String ownerLogin;
    private List<BranchModel> branches;
}
