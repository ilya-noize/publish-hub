package com.example.publishHub.model.comment;

public record CommentResponse(
        Long userId,
        Long postId,
        String content
) {
}
