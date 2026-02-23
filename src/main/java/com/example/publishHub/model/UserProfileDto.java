package com.example.publishHub.model;

public record UserProfileDto(
        Long id,
        String summary,
        Long userId
) {
}
