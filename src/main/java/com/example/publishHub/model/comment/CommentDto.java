package com.example.publishHub.model.comment;

public record CommentDto(
        Long id,
        Long userId,
        Long postId,
        String content
) {
}
