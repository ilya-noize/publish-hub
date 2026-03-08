package com.example.publishHub.service;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.comment.CommentDto;
import com.example.publishHub.model.comment.CommentMapper;
import com.example.publishHub.model.post.PostDto;
import com.example.publishHub.model.post.PostMapper;
import com.example.publishHub.model.post.PostShortDto;
import com.example.publishHub.repository.CommentRepository;
import com.example.publishHub.repository.PostRepository;
import com.example.publishHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogService {
    private static final Logger log = LogManager.getLogger(BlogService.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public List<UserEntity> createAll(List<UserEntity> users) {

        return userRepository.saveAll(users);
    }

    /**
     * Создание поста с начальными комментариями
     */
    public PostDto createPostWithComments(Long userId,
                                          PostDto postDto
    ) {
        log.debug("create Post with Comments. DTO:{}.", postDto);
        existsUserById(userId);
        if (!postDto.authorId().equals(userId)) {
            throw new IllegalArgumentException(
                    "The author's ID:%s and the current user's ID:%s must be the same."
                            .formatted(postDto.authorId(), userId));
        }
        Map<Long, UserEntity> commentators = getValidCommentators(postDto);
        PostEntity post = postMapper.toEntity(postDto);
        List<CommentEntity> comments = post.getComments().stream()
                .peek(comment -> {
                    Long keyId = comment.getUser().getId();
                    comment.setUser(commentators.get(keyId));
                })
                .toList();
        post.setComments(comments);
        postRepository.save(post);

        return postMapper.toDomain(post);
    }

    /**
     * Посты автора с количеством комментариев
     */
    public Map<Long, PostShortDto> getPostsByAuthor(Long userId) {
        log.debug("Get Posts by Author ID:{}.", userId);
        existsUserById(userId);
        return postRepository.findAllByAuthor_Id(userId)
                .stream()
                .map(postMapper::toDomain)
                .collect(Collectors.toMap(
                        PostDto::id,
                        post -> new PostShortDto(
                                post.title(),
                                (long) post.comments().size()
                        )
                ));
    }

    /**
     * Все комментарии пользователя
     */
    public List<CommentDto> getAllUserComments(Long userId) {
        existsUserById(userId);
        return commentRepository.findAllByUser_Id(userId).stream()
                .map(commentMapper::toDomain)
                .toList();
    }

    /**
     * Последние посты с комментариями
     */
    public List<PostDto> getRecentPostsWithComments(Long userId) {
        existsUserById(userId);
        throw new ResourceAccessException("Access Denied");
    }

    /**
     * Активность пользователя с постами и комментариями
     */
    public List<PostDto> getUserActivity(Long userId) {
        existsUserById(userId);
        throw new ResourceAccessException("Access Denied");
    }

    private Map<Long, UserEntity> getValidCommentators(PostDto postDto) {
        Set<Long> userIds = postDto.comments().stream()
            .map(CommentDto::userId)
            .collect(Collectors.toSet());
        Map<Long, UserEntity> existsUsers = userRepository.findAllByIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));
        List<Long> existsUserIds = new ArrayList<>(existsUsers.keySet().stream().toList());
        if (existsUserIds.size() != userIds.size()) {
            existsUserIds.forEach(userIds::remove);
            if (!userIds.isEmpty()) {
                String noSuchCommentatorsIds = userIds.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("No such commentators by ID:[%s]"
                        .formatted(noSuchCommentatorsIds));
            }
        }

        return existsUsers;
    }

    private void existsUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("No such user by ID:%s".formatted(userId));
        }
    }
}
