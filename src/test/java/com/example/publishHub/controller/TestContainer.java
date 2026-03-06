package com.example.publishHub.controller;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.CommentRequest;
import com.example.publishHub.model.PostCreateRequest;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Testcontainers
@SpringBootTest
public class TestContainer {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected CommentEntity getCommentEntity(UserEntity userEntity, PostEntity postEntity) {
        return CommentEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .content(getRandomString())
                .build();
    }

    protected CommentRequest getCommentRequest(Long userId, Long postId) {
        return new CommentRequest(userId, postId, getRandomString());
    }

    protected CommentDto getCommentDto(Long userId, Long postId) {
        return new CommentDto(null, userId, postId, getRandomString());
    }

    protected PostCreateRequest getPostRequest(UserEntity userEntity) {
        String title = UUID.randomUUID().toString().substring(0, 8);
        String content = getRandomString();
        return new PostCreateRequest(
                null,
                title,
                content,
                userEntity.getId(),
                List.of()
        );
    }

    protected PostDto getPostDto(UserEntity userEntity) {
        String title = UUID.randomUUID().toString().substring(0, 8);
        String content = getRandomString();
        return new PostDto(
                null,
                title,
                content,
                userEntity.getId(),
                new ArrayList<>()
        );
    }

    protected PostEntity getPostEntity(UserEntity userEntity) {
        String title = UUID.randomUUID().toString().substring(0, 8);
        String content = getRandomString();
        return PostEntity.builder()
                .title(title)
                .content(content)
                .author(userEntity)
                .build();
    }

    protected UserEntity getUserEntity() {
        String name = UUID.randomUUID().toString().substring(0, 8);
        UserEntity user = UserEntity.builder()
                .name(name)
                .email(name + "@mail.com")
                .build();
        return userRepository.save(user);
    }

    private static String getRandomString() {
        return UUID.randomUUID().toString();
    }
}
