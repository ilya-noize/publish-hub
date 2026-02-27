package com.example.publishHub;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.entity.UserProfileEntity;
import com.example.publishHub.model.CommentDto;
import com.example.publishHub.model.PostDto;
import com.example.publishHub.model.PostMapper;
import com.example.publishHub.model.PostShortDto;
import com.example.publishHub.service.BlogService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@Profile("dev")
@RequiredArgsConstructor
public class BlogDataInitializer {
    private static final Logger log = LogManager.getLogger(BlogDataInitializer.class);
    
    public static final int USER_COUNT = 10;
    public static final int POSTS_PER_USER = 5;
    public static final int TOTAL_POST_COUNT = USER_COUNT * POSTS_PER_USER;
    public static final int COMMENTS_PER_POST = 5;
    public static final int TOTAL_COMMENT_COUNT = TOTAL_POST_COUNT * COMMENTS_PER_POST;


    private final EasyRandomParameters parameters = new EasyRandomParameters()
            .randomize(field -> field.getName().equals("email"), () -> UUID.randomUUID() + "@test.com")
            .excludeField(field -> field.getName().equals("id"));
    private final EasyRandom easyRandom = new EasyRandom(parameters);

    private final BlogService blogService;
    private final PostMapper postMapper;


    @PostConstruct
    @Transactional
    public void init() {

        log.debug("Initialize resources ...");
        // Создайте тестовые посты с комментариями

        log.debug("Initialize users ...");
        final List<UserEntity> users = new ArrayList<>();
        IntStream.range(0, USER_COUNT).forEach(i -> users.add(createUser()));
        log.debug(" {} users saved.", blogService.createAll(users).size());

        log.debug("Initialize posts ... ");
        final List<PostEntity> posts = new ArrayList<>();
        for (UserEntity author : users) {
            IntStream.range(0, POSTS_PER_USER).forEach(i -> posts.add(createPost(author)));
        }


        log.debug("Initialize comments ... ");
        IntStream.range(0, TOTAL_COMMENT_COUNT).forEach(i -> createComment(users, posts));

        // Протестируйте все методы сервиса
        // createPostWithComments
        Map<Long, PostDto> postDtoMap = posts.stream()
                .map(postMapper::toDomain)
                .map(post -> {
                    PostDto postWithComments = blogService.createPostWithComments(post.authorId(), post);
                    log.debug("Post with Comments saved: {}\n", postWithComments);
                    return postWithComments;
                })
                .collect(Collectors.toMap(
                        PostDto::id, post -> post
                ));

        // addCommentToPost
        Long postId = getPostId(postDtoMap);
        CommentDto commentToPost = new CommentDto(
                null,
                getUserId(users),
                postId,
                easyRandom.nextObject(String.class)
        );

        CommentDto addedCommentToPost = blogService.addCommentToPost(
                commentToPost.userId(),
                commentToPost.postId(),
                commentToPost
        );
        log.debug("Add Comment by Post ID:{} saved:{}\n", commentToPost.postId(), addedCommentToPost);

        //getPostsByAuthor
        Map<Long, PostShortDto> postsByAuthor = blogService
                .getPostsByAuthor(getUserId(users));
        log.debug("Post by Author:{}\n", postsByAuthor);

        //getPostWithComments
        PostDto postWithComments = blogService.getPostWithComments(getPostId(postDtoMap));
        log.debug("Post with Comments:{}", postWithComments);

        //approveComment - автор поста подтверждает коммент другого пользователя
        Long userId = postDtoMap.get(postId).authorId();
        CommentDto approveComment = blogService.approveComment(
                userId,
                postId,
                addedCommentToPost.id()
        );
        log.debug("Approve Comment:{} (Author ID:{}, PostID:{})", approveComment, userId, postId);


        //getPostsByAuthor
        Map<Long, PostShortDto> postsByAuthorTwice = blogService.getPostsByAuthor(userId);
        log.debug("Post by Author:{}", postsByAuthorTwice);

        // Проверьте работу LAZY загрузки
        // Убедитесь, что каскадные операции работают
    }

    private Long getPostId(Map<Long, PostDto> postDtoMap) {
        List<PostDto> values = postDtoMap.values().stream().toList();
        return values.get(easyRandom.nextInt(values.size())).id();
    }

    private Long getUserId(List<UserEntity> users) {
        return users.get(easyRandom.nextInt(users.size())).getId();
    }

    private void createComment(
            List<UserEntity> users,
            List<PostEntity> posts
    ) {
        CommentEntity comment = easyRandom.nextObject(CommentEntity.class);
        PostEntity post = posts.get(easyRandom.nextInt(posts.size()));
        comment.setPost(post);

        Predicate<UserEntity> userIsAuthor = u -> u.getId().equals(post.getAuthor().getId());
        List<UserEntity> commentators = users
                .stream()
                .filter(userIsAuthor)
                .toList();
        if (commentators.isEmpty()) commentators = users;
        UserEntity user = commentators.get(easyRandom.nextInt(commentators.size()));
        comment.setUser(user);

        comment.setId(null);
        comment.setCreatedAt(LocalDateTime.now());
        post.getComments().add(comment);
        log.debug("Generate Comment:{}", comment);
    }

    private PostEntity createPost(
            UserEntity author
    ) {
        PostEntity post = easyRandom.nextObject(PostEntity.class);
        post.setId(null);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        log.debug("Generate Post:{}", post);
        return post;
    }

    private UserEntity createUser() {
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        UserProfileEntity userProfile = easyRandom.nextObject(UserProfileEntity.class);
        user.setId(null);
        userProfile.setId(null);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);
        log.debug("Generate User:{}", user);
        return user;
    }
}
