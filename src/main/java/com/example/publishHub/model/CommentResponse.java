package com.example.publishHub.model;

public record CommentResponse(
        Long userId,
        Long postId,
        String content
) {
}
