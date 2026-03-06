package com.example.publishHub.model;

public record PostProjectionDto(
        Long id,
        String title,
        Long commentCount
) {
}
