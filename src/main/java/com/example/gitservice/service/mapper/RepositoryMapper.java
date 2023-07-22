package com.example.gitservice.service.mapper;

import com.example.gitservice.dto.RepositoryDTO;
import com.example.gitservice.model.RepositoryModel;
import org.springframework.stereotype.Component;

@Component
public class RepositoryMapper {
    public RepositoryModel convertRepository(RepositoryDTO dto) {
        RepositoryModel result = new RepositoryModel();
        result.setName(dto.getName());
        result.setOwnerLogin(dto.getOwner().getLogin());
        return result;
    }
}
