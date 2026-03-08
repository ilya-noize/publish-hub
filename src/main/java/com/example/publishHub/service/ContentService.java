package com.example.publishHub.service;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.comment.CommentDto;
import com.example.publishHub.model.comment.CommentMapper;
import com.example.publishHub.model.post.PostDto;
import com.example.publishHub.model.post.PostMapper;
import com.example.publishHub.model.post.PostProjectionDto;
import com.example.publishHub.model.post.PostSimpleDto;
import com.example.publishHub.model.user.UserPostCommentIDsParameters;
import com.example.publishHub.repository.CommentRepository;
import com.example.publishHub.repository.PostRepository;
import com.example.publishHub.repository.PostWithCommentCountProjection;
import com.example.publishHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.awt.print.Pageable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContentService {
    private static final Logger log = LogManager.getLogger(ContentService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public Page<PostDto> getAllPosts(Pageable pageable) {
        throw new ResourceAccessException("Access Denied");
    }

    /** Получение поста по ID     */
    public PostSimpleDto getById(Long postId) {
        log.debug("Get Post. ID:{}.", postId);
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No such post by ID:%s".formatted(postId)
                ));
        return postMapper.toSimpleDomain(postEntity);
    }

    /** Получение поста автора со всеми комментариями     */
    public PostDto getPostWithComments(UserPostCommentIDsParameters userPostCommentIDsParameters) {
        Long postId = userPostCommentIDsParameters.postId();
        Long authorId = userPostCommentIDsParameters.userId();
        log.debug("Get author's post with Comments. post ID:{}, author ID:{}.", postId, authorId);
        PostEntity postEntity = postRepository.findByIdAndAuthor_Id(postId, authorId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No such post by ID:%s".formatted(postId)
                ));
        return postMapper.toDomain(postEntity);
    }

    /** Получение поста со всеми комментариями     */
    public PostDto getPostComments(Long postId) {
        log.debug("Get Post with Comments. ID:{}.", postId);
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No such post by ID:%s".formatted(postId)
                ));
        return postMapper.toDomain(postEntity);
    }

    /** Получение комментария поста     */
    public CommentDto getPostCommentById(Long postId, Long commentId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException("No such post by ID:%s".formatted(postId));
        }
        CommentEntity comment = commentRepository.findByIdAndPost_IdAndApprovedNot(commentId, postId, true)
                .orElseThrow(() -> new NoSuchElementException("No such approved comment ID:%s in Post ID:%s"
                        .formatted(commentId, postId)
                ));
        return commentMapper.toDomain(comment);
    }

        /** Удаление всех комментариев из поста     */
    public void deletePostComments(UserPostCommentIDsParameters parameters) {
        validateParameters(parameters);
        Long userId = parameters.userId();
        Long postId = parameters.postId();
        int deleted = commentRepository.deleteByPost_Id(postId);
        log.debug("Removed {} comments in post ID:{}, author ID:{}", deleted, postId, userId);
    }

    /** Удаление комментария по ID     */
    public void deletePostCommentsById(UserPostCommentIDsParameters parameters) {
        validateParameters(parameters);

        Long commentId = parameters.commentId();
        Long userId = parameters.userId();
        Long postId = parameters.postId();
        if (!commentRepository.existsByIdAndUser_IdAndPost_Id(commentId, postId, userId)) {
            throw new NoSuchElementException("No such comment by ID:%s in post by ID:%s , author ID:%s"
                    .formatted(commentId, postId, userId)
            );
        }

        int deleted = commentRepository.deleteByIdAndPost_IdAndUser_Id(commentId, postId, userId);
        log.debug("Removed {} comments by ID:{} in post ID:{}, author ID:{}", deleted, commentId, postId, userId);
    }

    /** Комментарий к посту по ID     */
    public CommentDto getPostCommentsById(UserPostCommentIDsParameters parameters) {
        validateParameters(parameters);
        Long commentId = parameters.commentId();
        Long userId = parameters.userId();
        Long postId = parameters.postId();
        CommentEntity comment = commentRepository.findByIdAndUser_IdAndPost_Id(commentId, userId, postId).orElseThrow(
                () -> new NoSuchElementException("No such comment By ID:%s, author by ID:%s in post by ID:%s"
                        .formatted(commentId, userId, postId)
                ));

        return commentMapper.toDomain(comment);
    }

    /** Посты по тегу с комментариями и авторами     */
    public List<PostDto> getPostsByTagWithDetails(List<String> tags) {
        List<PostEntity> byTagsWithComments = postRepository.findByTagsWithComments(tags);

        return byTagsWithComments.stream()
                .map(postMapper::toDomain)
                .toList();
    }

    /** Популярные посты     */
    public List<PostProjectionDto> getPopularPosts() {
        List<PostWithCommentCountProjection> popularPosts = postRepository.findPopularPosts();

        return popularPosts.isEmpty() ? List.of()
                : popularPosts.stream()
                .dropWhile(Objects::isNull)
                .map(this::toProjection)
                .toList();
    }

    /** Подтверждение комментария     */
    @Transactional
    public CommentDto validateComment(UserPostCommentIDsParameters parameters,
                                      Boolean isApproved
    ) {
        Long commentId = parameters.commentId();
        Long userId = parameters.userId();
        Long postId = parameters.postId();

        log.debug("Approve Comment ID:{} User's ID:{} by Post ID:{}.", commentId, userId, postId);

        CommentEntity comment = commentRepository.findByIdAndPost_IdAndApprovedNot(commentId, postId, true)
                .orElseThrow(() -> new NoSuchElementException("No such approved comment ID:%s in Post ID:%s"
                        .formatted(commentId, postId)
                ));
        CommentDto domain = commentMapper.toDomain(comment);
        validateRequestParameters(parameters, domain);
        comment.setApproved(isApproved);

        return commentMapper.toDomain(commentRepository.save(comment));
    }

    /** Добавление комментария к посту */
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

    /** Валидация переменных пути */
    private void validateParameters(UserPostCommentIDsParameters parameters) {
        if (!userRepository.existsById(parameters.userId())) {
            throw new NoSuchElementException("No such user by ID:%s".formatted(parameters.userId()));
        }
        if (!postRepository.existsByIdAndAuthor_Id(parameters.postId(), parameters.userId())) {
            throw new NoSuchElementException("User by ID:%s is not author's post by ID:%s"
                    .formatted(parameters.userId(), parameters.postId())
            );
        }
    }

    /** Конвертор Проекции в ДТО     */
    private PostProjectionDto toProjection(PostWithCommentCountProjection projection) {
        return new PostProjectionDto(
                projection.getId(),
                projection.getTitle(),
                projection.getCommentCount()
        );
    }

    /** @see com.example.publishHub.controller.PostController#getPostsByAllTags() */
    public List<PostDto> getPostTags() {
        throw new ResourceAccessException("Access Denied");
    }
}
