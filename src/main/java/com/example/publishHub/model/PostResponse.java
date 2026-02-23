package com.example.publishHub.model;

import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        UserDto author,
        List<CommentDto> comments
) {
}
