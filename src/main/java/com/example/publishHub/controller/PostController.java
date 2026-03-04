package com.example.publishHub.controller;

import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostRequest;
import com.example.publishHub.model.PostResponse;
import com.example.publishHub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

//@RestController
//@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping
    public Page<PostResponse> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt")Pageable pageable
    ){
        Page<PostDto> posts = postService.getAllPosts(pageable);
        return posts.map(postMapper::toResponse);
    }

    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable Long id    ) {
        PostDto post = postService.getById(id);
        return postMapper.toResponse(post);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@RequestBody PostRequest request) {
        PostDto domain = postMapper.toDomain(request);

        return postMapper.toResponse(postService.createPost(domain));
    }


}
