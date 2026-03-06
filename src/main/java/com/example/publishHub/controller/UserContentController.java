package com.example.publishHub.controller;

import com.example.publishHub.model.PostCreateRequest;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostResponse;
import com.example.publishHub.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/posts")
@RequiredArgsConstructor
public class UserContentController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public PostResponse createPostWithComments(
            @PathVariable Long userId,
            @RequestBody @Valid PostCreateRequest postCreateRequest
    ) {
        PostDto domain = postMapper.toDomain(postCreateRequest);
        PostDto postWithComments = postService.createPostWithComments(userId, domain);

        return postMapper.toResponse(postWithComments);
    }
}
