package com.example.publishHub.service;

import com.example.publishHub.model.UserPostCommentIDsParameters;
import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.repository.CommentRepository;
import com.example.publishHub.repository.PostRepository;
import com.example.publishHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final Logger log = LogManager.getLogger(CommentService.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    /** Подтверждение комментария     */
    @Transactional
    public CommentDto validateComment(UserPostCommentIDsParameters parameters, Boolean isApproved) {
        Long commentId = parameters.commentId();
        Long userId = parameters.userId();
        Long postId = parameters.postId();

        log.debug("Approve Comment ID:{} User's ID:{} by Post ID:{}.", commentId, userId, postId);

        CommentEntity comment = commentRepository.findByIdAndPost_IdAndApprovedNot(commentId, postId, true)
                .orElseThrow(() -> new NoSuchElementException("No such approved comment ID:%s in Post ID:%s"
                        .formatted(commentId, postId)
                ));
        PostEntity post = comment.getPost();
        if (!post.getId().equals(postId)) {
            throw new IllegalArgumentException(
                    "The post's ID:%s by comment and the current post's ID:%s must be the same."
                            .formatted(post.getId(), postId));
        }
        Long authorId = post.getAuthor().getId();
        if (!authorId.equals(userId)) {
            throw new IllegalArgumentException(
                    "The post author's ID:%s and the current user's ID:%s must be the same."
                            .formatted(authorId, userId));
        }
        comment.setApproved(isApproved);

        return commentMapper.toDomain(commentRepository.save(comment));
    }

    // Добавление комментария к посту
    @Transactional
    public CommentDto addCommentToPost(UserPostCommentIDsParameters parameters,
                                       CommentDto commentDto
    ) {
        log.debug("Add Comment to Post. DTO:{}.", commentDto);
        validateRequestParameters(parameters, commentDto);
        UserEntity user = userRepository.findById(commentDto.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No such user by ID:%s".formatted(commentDto.userId())
                ));
        PostEntity post = postRepository.findById(commentDto.postId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No such post by ID:%s".formatted(commentDto.postId())
                ));
        CommentEntity comment = commentRepository.save(CommentEntity.builder()
                .post(post)
                .user(user)
                .content(commentDto.content())
                .build());

        return commentMapper.toDomain(comment);
    }

    private void validateRequestParameters(UserPostCommentIDsParameters parameters, CommentDto commentDto) {
        Long userId = parameters.userId();
        Long postId = parameters.postId();
        if (!commentDto.userId().equals(userId)) {
            throw new IllegalArgumentException(
                    "The commentator's ID:%s and the current user's ID:%s must be the same."
                            .formatted(commentDto.userId(), userId)
            );
        } else if (!commentDto.postId().equals(postId)) {
            throw new IllegalArgumentException(
                    "The post's ID:%s and the current post's ID:%s must be the same."
                            .formatted(commentDto.postId(), postId)
            );
        }
    }
}
