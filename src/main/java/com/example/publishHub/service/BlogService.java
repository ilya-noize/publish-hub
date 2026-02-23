package com.example.publishHub.service;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.model.UserMapper;
import com.example.publishHub.repository.CommentRepository;
import com.example.publishHub.repository.PostRepository;
import com.example.publishHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public BlogService(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            UserMapper userMapper,
            PostMapper postMapper,
            CommentMapper commentMapper
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    /**
     * Создание поста с начальными комментариями
     *
     * @param userId
     * @param postDto
     * @return
     */
    public PostDto createPostWithComments(Long userId, PostDto postDto) {
        if (!postDto.author().id().equals(userId)) {
            throw new IllegalArgumentException("The author's ID:%s and the current user's ID:%s must be the same."
                    .formatted(postDto.author().id(), userId));
        }
        PostEntity post = postMapper.toEntity(postDto);
        postRepository.save(post);

        return postMapper.toDomain(post);
    }

    /**
     * Добавление комментария к посту
     *
     * @return
     */
    public CommentDto addCommentToPost(Long userId, Long postId, CommentDto commentDto) {
        if (!commentDto.userId().equals(userId)) {
            throw new IllegalArgumentException(
                    "The commentator's ID:%s and the current user's ID:%s must be the same."
                            .formatted(
                                    commentDto.userId(), userId
                            ));
        } else if (!commentDto.postId().equals(postId)) {
            throw new IllegalArgumentException(
                    "The post's ID:%s and the current post's ID:%s must be the same."
                            .formatted(
                                    commentDto.postId(), postId
                            ));
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No such user by ID:%s".formatted(userId)
                ));
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No such post by ID:%s".formatted(postId)
                ));
        CommentEntity comment = commentRepository.save(CommentEntity.builder()
                .post(post)
                .user(user)
                .content(commentDto.content())
                .build());

        return commentMapper.toDomain(comment);
    }

    // - getPostWithComments (получение поста со всеми комментариями)
    public PostDto getPostWithComments(Long postId) {
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("No such post by ID:%s"
                        .formatted(postId)
                ));

        return postMapper.toDomain(postEntity);
    }

    /**
     * Подтверждение комментария
     *
     * @param postId
     * @param commentId
     * @return
     * @throws IllegalArgumentException Если postId не равен postId комментария
     * @throws IllegalArgumentException Если authorId из post не равен userId
     * @throws NoSuchElementException   Если не найдет неподтверждённый комментарий
     */
    public CommentDto approveComment(Long userId, Long postId, Long commentId) {
        CommentEntity comment = Optional.ofNullable(
                        commentRepository.findByIdAndPost_IdAndApprovedNot(commentId, postId, true))
                .orElseThrow(() -> new NoSuchElementException("No such approved comment ID:%s in Post ID:%s"
                        .formatted(commentId, postId)
                ));
        PostEntity post = comment.getPost();
        if (!post.getId().equals(postId)) {
            throw new IllegalArgumentException(
                    "The post's ID:%s by comment and the current post's ID:%s must be the same."
                            .formatted(post.getId(), postId));
        }
        UserEntity author = post.getAuthor();
        if (author.getId().equals(userId)) {
            throw new IllegalArgumentException(
                    "The post author's ID:%s and the current user's ID:%s must be the same."
                            .formatted(author.getId(), userId));
        }
        comment.setApproved(true);

        return commentMapper.toDomain(commentRepository.save(comment));
    }

    /**
     * Посты автора с количеством комментариев
     *
     * @param userId authorId ID
     * @return
     */
    public Map<Long, PostShortDto> getPostsByAuthor(Long userId) {

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
}
