package com.example.gitservice.service.mapper;

import com.example.gitservice.dto.RepositoryDTO;
import com.example.gitservice.model.RepositoryModel;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RepositoryMapper {
    public RepositoryModel convertRepository(RepositoryDTO dto) {
        return new RepositoryModel(dto.name(), dto.owner().login(), Collections.emptyList());
    }
}
