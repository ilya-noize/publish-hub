package com.example.publishHub.controller;

import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.CommentResponse;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostResponse;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class UserStatisticController {
    private final PostMapper postMapper;
    private final BlogService blogService;

    private final CommentMapper commentMapper;

    @GetMapping("/posts")
    public Map<Long, PostShortDto> getPostsByAuthor(
            @PathVariable Long userId
    ) {
        Map<Long, PostShortDto> postsByAuthor = blogService.getPostsByAuthor(userId);
        return postsByAuthor.isEmpty() ? null
                : postsByAuthor;
    }

    @GetMapping("/comments")
    public List<CommentResponse> getAllUserComments(
            @PathVariable Long userId
    ) {
        return blogService.getAllUserComments(userId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @GetMapping("/recent")
    public List<PostResponse> getRecentPostsWithComments(
            @PathVariable Long userId
    ) {
        List<PostDto> recentPostsWithComments = blogService.getRecentPostsWithComments(userId);
        return recentPostsWithComments.isEmpty() ? List.of()
                : recentPostsWithComments.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @GetMapping("/activity")
    public List<PostResponse> getUserActivity(
            @PathVariable Long userId
    ) {
        List<PostDto> userActivity = blogService.getUserActivity(userId);

        return userActivity.isEmpty() ? List.of()
                : userActivity.stream()
                .map(postMapper::toResponse)
                .toList();
    }

}
