package com.example.gitservice.dto;

public record RepositoryDTO(String name, boolean fork, OwnerDTO owner) {
}
