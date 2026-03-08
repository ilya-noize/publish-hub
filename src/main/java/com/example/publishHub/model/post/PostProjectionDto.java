package com.example.publishHub.model.post;

public record PostProjectionDto(
        Long id,
        String title,
        Long commentCount
) {
}
