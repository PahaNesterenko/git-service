package com.example.gitservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDTO {
    private String name;
    private boolean fork;
    private OwnerDTO owner;
}
