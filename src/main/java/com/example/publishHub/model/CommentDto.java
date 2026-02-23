package com.example.publishHub.model;

public record CommentDto(
        Long id,
        Long userId,
        Long postId,
        String content
) {
}
