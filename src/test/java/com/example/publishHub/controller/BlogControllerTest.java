package com.example.publishHub.controller;

import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentMapper;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.repository.UserRepository;
import com.example.publishHub.service.BlogService;
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
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createPostWithoutComments() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto postDto = getPostWithComments(author, users);
        PostDto postWithComments = blogService.createPostWithComments(author.getId(), postDto);

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
        PostDto post = blogService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );
        UserEntity commentator = users.get(0);
        CommentDto comment = blogService.addCommentToPost(
                commentator.getId(),
                post.id(),
                getCommentDto(commentator.getId(), post.id())
        );
        Assertions.assertNotNull(comment.id());
    }

    @Test
    void shouldReturnListCommentsInPost() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto post = blogService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );

        PostDto postWithComments = blogService.getPostWithComments(post.id());
        Assertions.assertEquals(10, postWithComments.comments().size());
    }

    @Test
    void approveComment() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        PostDto post = blogService.createPostWithComments(
                author.getId(),
                getPostWithComments(author, users)
        );
        UserEntity commentator = users.get(0);
        CommentDto comment = blogService.addCommentToPost(
                commentator.getId(),
                post.id(),
                getCommentDto(commentator.getId(), post.id())
        );
        CommentDto commentApprove = blogService.approveComment(author.getId(), post.id(), comment.id());

    }

    @Test
    void getPostsByAuthor() {
        List<UserEntity> users = getUserEntities();
        UserEntity author = users.get(2);
        blogService.createPostWithComments(
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
}