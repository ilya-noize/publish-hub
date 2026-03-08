package com.example.publishHub.controller;

import com.example.publishHub.model.comment.CommentDto;
import com.example.publishHub.model.comment.CommentMapper;
import com.example.publishHub.model.comment.CommentRequest;
import com.example.publishHub.model.comment.CommentResponse;
import com.example.publishHub.model.post.PostDto;
import com.example.publishHub.model.post.PostMapper;
import com.example.publishHub.model.user.UserPostCommentIDsParameters;
import com.example.publishHub.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class UserCommentController {
    private final ContentService contentService;

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @PostMapping
    public CommentResponse addCommentToPost(
            UserPostCommentIDsParameters userPostCommentIDsParameters,
            @RequestBody @Valid CommentRequest request
    ) {
        CommentDto commentDto = contentService.addCommentToPost(
                userPostCommentIDsParameters,
                commentMapper.toDomain(request)
        );

        return commentMapper.toResponse(commentDto);
    }

    @GetMapping
    public List<CommentResponse> getUsersCommentsInPost(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        PostDto postWithComments = contentService.getPostWithComments(userPostCommentIDsParameters);

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

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostComments(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        contentService.deletePostComments(userPostCommentIDsParameters);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostCommentsById(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        contentService.deletePostCommentsById(userPostCommentIDsParameters);
    }

    @GetMapping("/{commentId}")
    public CommentResponse getPostCommentsById(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        CommentDto postCommentsById = contentService.getPostCommentsById(userPostCommentIDsParameters);

        return commentMapper.toResponse(postCommentsById);
    }

    @PatchMapping("/{commentId}/approve")
    public CommentResponse approveComment(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        CommentDto dto = contentService.validateComment(userPostCommentIDsParameters, true);
        return commentMapper.toResponse(dto);
    }

    @PatchMapping("/{commentId}/reject")
    public CommentResponse rejectComment(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        CommentDto dto = contentService.validateComment(userPostCommentIDsParameters, false);
        return commentMapper.toResponse(dto);
    }

}

