package com.example.publishHub.model;

import java.util.List;

public record PostRequest(
        Long id,
        String title,
        String content,
        Long authorId,
        List<CommentDto> comments
) {
}
