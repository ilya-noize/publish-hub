package com.example.publishHub.service;

import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.repository.CommentRepository;
import com.example.publishHub.repository.PostRepository;
import com.example.publishHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    /** Посты автора с количеством комментариев     */
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

    /** Все комментарии пользователя    */
    public List<CommentDto> getAllUserComments(Long userId) {
        existsUserById(userId);
        return commentRepository.findAllByUser_Id(userId).stream()
                .map(commentMapper::toDomain)
                .toList();
    }

    /** Последние посты с комментариями     */
    public List<PostDto> getRecentPostsWithComments(Long userId) {
        existsUserById(userId);
        return List.of();
    }

    /** Активность пользователя с постами и комментариями     */
    public List<PostDto> getUserActivity(Long userId) {
        existsUserById(userId);
        return null;
    }

    private void existsUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("No such user by ID:%s".formatted(userId));
        }
    }
}
