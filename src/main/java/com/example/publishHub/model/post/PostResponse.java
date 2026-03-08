package com.example.publishHub.model.post;

import com.example.publishHub.model.user.UserDto;
import com.example.publishHub.model.comment.CommentDto;

import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        UserDto author,
        List<CommentDto> comments
) {
}
