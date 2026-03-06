package com.example.publishHub.model;

import jakarta.validation.constraints.Positive;

public record UserPostCommentIDsParameters(
        @Positive
        Long userId,

        @Positive
        Long postId,

        @Positive
        Long commentId
) {
}
