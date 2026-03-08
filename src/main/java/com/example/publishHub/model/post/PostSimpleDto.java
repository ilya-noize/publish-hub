package com.example.publishHub.model.post;

public record PostSimpleDto(
        Long id,
        String title,
        String content,
        Long authorId,
        Long commentCount
) {
}
