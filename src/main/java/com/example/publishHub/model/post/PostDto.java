package com.example.publishHub.model.post;

import com.example.publishHub.model.comment.CommentDto;

import java.util.List;

public record PostDto(
        Long id,
        String title,
        String content,
        Long authorId,
        List<CommentDto> comments//,        List<TagDto> tags
) {
}
