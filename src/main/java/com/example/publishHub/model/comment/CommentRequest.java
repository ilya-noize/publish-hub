package com.example.publishHub.model.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CommentRequest(
        @Positive @NotNull
        Long userId,

        @Positive @NotNull
        Long postId,

        @NotBlank
        String content
) {
}
