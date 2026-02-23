package com.example.publishHub.controller;

import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.CommentRequest;
import com.example.publishHub.model.CommentResponse;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostRequest;
import com.example.publishHub.model.PostResponse;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BlogController {
    private final BlogService blogService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public BlogController(
            BlogService blogService, PostMapper postMapper,
            CommentMapper commentMapper
    ) {
        this.blogService = blogService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    @PostMapping("/users/{userId}/posts")
    public PostResponse createPostWithComments(
            @PathVariable Long userId,
            @RequestBody @Valid PostRequest postRequest
    ) {
        PostDto domain = postMapper.toDomain(postRequest);
        PostDto postWithComments = blogService.createPostWithComments(userId, domain);

        return postMapper.toResponse(postWithComments);
    }

    @PostMapping("/users/{userId}/posts/{postId}")
    public CommentResponse addCommentToPost(
            @PathVariable("userId") Long userId,
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CommentRequest request
    ) {
        CommentDto commentDto = blogService.addCommentToPost(
                userId,
                postId,
                commentMapper.toDomain(request)
        );

        return commentMapper.toResponse(commentDto);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse getPostWithComments(
            @PathVariable Long postId
    ) {
        PostDto postWithComments = blogService.getPostWithComments(postId);

        return postMapper.toResponse(postWithComments);
    }


    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponse> getPostComments(
            @PathVariable Long postId
    ) {
        PostDto postWithComments = blogService.getPostWithComments(postId);

        return postMapper.toResponse(postWithComments)
                .comments()
                .stream()
                .map(c -> new CommentResponse(
                        c.userId(),
                        c.postId(),
                        c.content()
                ))
                .toList();
    }

    @PatchMapping("/users/{userId}/posts/{postId}/comments/{commentId}")
    public CommentResponse approveComment(
            @PathVariable Long userId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        return commentMapper.toResponse(
                blogService.approveComment(userId, postId, commentId)
        );
    }

    @GetMapping("/users/{userId}")
    public Map<Long, PostShortDto> getPostsByAuthor(
            @PathVariable("userId") Long userId
    ) {
        return blogService.getPostsByAuthor(userId);
    }
}
