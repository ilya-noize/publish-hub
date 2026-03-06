package com.example.publishHub.controller;

import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.model.UserPostCommentIDsParameters;
import com.example.publishHub.repository.UserRepository;
import com.example.publishHub.service.BlogService;
import com.example.publishHub.service.CommentService;
import com.example.publishHub.service.PostService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


class BlogControllerTest extends TestContainer {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createPostWithoutComments() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto postDto = getPostWithComments(author, users);
        PostDto postWithComments = postService.createPostWithComments(author.getId(), postDto);

        Long postId = postWithComments.id();
        Assertions.assertNotNull(postId);
        postWithComments.comments().forEach(commentDto -> {
            Assertions.assertEquals(commentDto.postId(), postId);
            Assertions.assertNotNull(commentDto.id());
        });
    }

    @Test
    void addCommentToPost() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto post = postService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );
        UserEntity commentator = users.get(0);
        CommentDto comment = commentService.addCommentToPost(
                new UserPostCommentIDsParameters(commentator.getId(), post.id(), null),
                getCommentDto(commentator.getId(), post.id())
        );
        Assertions.assertNotNull(comment.id());
    }

    @Test
    void shouldReturnListCommentsInPost() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto post = postService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );

        PostDto postWithComments = postService.getPostWithComments(new UserPostCommentIDsParameters(author.getId(), post.id(), null));
        Assertions.assertEquals(10, postWithComments.comments().size());
    }

    @Test
    void approveComment() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto post = postService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );
        UserEntity commentator = users.get(0);
        CommentDto comment = commentService.addCommentToPost(
                new UserPostCommentIDsParameters(commentator.getId(), post.id(), null),
                getCommentDto(commentator.getId(), post.id())
        );
        CommentDto commentApprove = commentService.validateComment(
                new UserPostCommentIDsParameters(author.getId(), post.id(), comment.id()),
                true
        );
    }

    @Test
    void getPostsByAuthor() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        postService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );
        Map<Long, PostShortDto> postsByAuthor = blogService.getPostsByAuthor(author.getId());
        Assertions.assertEquals(1, postsByAuthor.size());
    }

    private PostDto getPostWithComments(UserEntity author, List<UserEntity> users) {
        PostDto postDto = getPostDto(author);
        IntStream.range(0, 10).forEach(i -> {
            UserEntity user = users.get(i % 2);
            postDto.comments().add(i, getCommentDto(user.getId(), null));
        });
        return postDto;
    }

    private @NotNull List<UserEntity> getUserEntities() {
        List<UserEntity> users = new ArrayList<>();
        IntStream.range(0, 3).forEach(i ->
                users.add(i, userRepository.save(getUserEntity()))
        );
        return users;
    }

    @Test
    void shouldNotProduceNPlusOneQueries() {
        // TODO: Напишите тесты которые проверяют:
        // - Количество SQL запросов при загрузке постов с комментариями
        // - Отсутствие дополнительных запросов при обращении к связанным данным
        // - Корректность загруженных данных
    }
}