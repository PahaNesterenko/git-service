package com.example.gitservice.service.mapper;

import com.example.gitservice.dto.BranchDTO;
import com.example.gitservice.model.BranchModel;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {
    public BranchModel convertBranch(BranchDTO dto) {
        return new BranchModel(dto.name(), dto.commit().sha());
    }
}
