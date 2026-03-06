package com.example.publishHub.controller;

import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.CommentResponse;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostResponse;
import com.example.publishHub.model.PostSimpleDto;
import com.example.publishHub.model.TagResponse;
import com.example.publishHub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/posts")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public Page<PostResponse> getAllPosts(
            @PageableDefault(sort = "createdAt")
            Pageable pageable
    ) {
        Page<PostDto> posts = postService.getAllPosts(pageable);
        return posts.map(postMapper::toResponse);
    }

    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable Long postId) {
        PostSimpleDto post = postService.getById(postId);

        return postMapper.toResponse(post);
    }

    @GetMapping("/{postId}/comments")
    public PostResponse getPostComments(
            @PathVariable Long postId
    ) {
        PostDto comment = postService.getPostComments(postId);

        return postMapper.toResponse(comment);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public CommentResponse getPostCommentById(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        CommentDto comment = postService.getPostCommentById(postId, commentId);

        return commentMapper.toResponse(comment);
    }

    @GetMapping("/tags")
    public List<TagResponse> getPostsByAllTags() {
        // todo: выводить теги отмеченные в постах
        //  с сортировкой (по числу комментариев, дате)
        //  [
        //      {
        //       "tag":"tag",
        //       "post":
        //        [
        //         {
        //          "id":"1",
        //          "title":"title",
        //          "comment_count":"125",
        //          "created_at":"2020-05-01T14:51:44.123"
        //         }, ...
        //        ]
        //      }
        //  ]
        return null;
    }

    @GetMapping("/tags/{tags}")
    public List<PostResponse> getPostsByTagWithDetails(@PathVariable List<String> tags) {
        List<PostDto> postsByTagWithDetails = postService.getPostsByTagWithDetails(tags);
        return postsByTagWithDetails.isEmpty()
                ? List.of()
                : postsByTagWithDetails.stream()
                .map(postMapper::toResponse)
                .toList();
    }
}
