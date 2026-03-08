package com.example.publishHub;

import com.example.publishHub.entity.CommentEntity;
import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import com.example.publishHub.entity.UserProfileEntity;
import com.example.publishHub.model.comment.CommentDto;
import com.example.publishHub.model.post.PostDto;
import com.example.publishHub.model.post.PostMapper;
import com.example.publishHub.model.post.PostShortDto;
import com.example.publishHub.model.user.UserPostCommentIDsParameters;
import com.example.publishHub.service.BlogService;
import com.example.publishHub.service.ContentService;
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
    public static final int USER_COUNT = 10;
    public static final int POSTS_PER_USER = 5;
    public static final int TOTAL_POST_COUNT = USER_COUNT * POSTS_PER_USER;
    public static final int COMMENTS_PER_POST = 5;
    public static final int TOTAL_COMMENT_COUNT = TOTAL_POST_COUNT * COMMENTS_PER_POST;
    private static final Logger log = LogManager.getLogger(BlogDataInitializer.class);

    private final EasyRandomParameters parameters = new EasyRandomParameters()
            .randomize(field -> field.getName().equals("email"), () -> UUID.randomUUID() + "@test.com")
            .excludeField(field -> field.getName().equals("id"));
    private final EasyRandom easyRandom = new EasyRandom(parameters);

    private final BlogService blogService;
    private final ContentService contentService;

    private final PostMapper postMapper;

    @PostConstruct
    @Transactional
    public void init() {

        log.debug("Initialize resources ...");
        // Создайте тестовые посты с комментариями

        log.debug("Initialize users ...");
        final List<UserEntity> users = initializeUsers();
        log.debug(" {} users saved.", blogService.createAll(users).size());

        log.debug("Initialize posts ... ");
        final List<PostEntity> posts = initializePosts(users);


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
        Long userId = users.get(generateIndex(users)).getId();
        CommentDto commentToPost = new CommentDto(
                null,
                userId,
                postId,
                easyRandom.nextObject(String.class)
        );

        CommentDto addedCommentToPost = contentService.addCommentToPost(
                new UserPostCommentIDsParameters(commentToPost.userId(), commentToPost.postId(), commentToPost.id()),
                commentToPost
        );
        log.debug("Add Comment by Post ID:{} saved:{}\n", commentToPost.postId(), addedCommentToPost);

        //getPostsByAuthor
        Map<Long, PostShortDto> postsByAuthor = blogService
                .getPostsByAuthor(userId);
        log.debug("Post by Author:{}\n", postsByAuthor);

        //getPostWithComments
        Long postIdByGet = getPostId(postDtoMap);
        Long authorId = postDtoMap.get(postIdByGet).authorId();
        PostDto postWithComments = contentService.getPostWithComments(new UserPostCommentIDsParameters(authorId, postIdByGet, null));
        log.debug("Post with Comments:{}", postWithComments);

        //approveComment - автор поста подтверждает коммент другого пользователя
        authorId = postDtoMap.get(postId).authorId();
        CommentDto approveComment = contentService.validateComment(
                new UserPostCommentIDsParameters(authorId, postId, addedCommentToPost.id()),
                true
        );
        log.debug("Approve Comment:{} (Author ID:{}, PostID:{})", approveComment, approveComment.userId(), approveComment.postId());


        //getPostsByAuthor
        Map<Long, PostShortDto> postsByAuthorTwice = blogService.getPostsByAuthor(userId);
        log.debug("Post by Author:{}", postsByAuthorTwice);

        // Проверьте работу LAZY загрузки
        // Убедитесь, что каскадные операции работают
    }

    private Long getPostId(Map<Long, PostDto> postDtoMap) {
        List<PostDto> values = postDtoMap.values().stream().toList();
        return values.get(generateIndex(values)).id();
    }

    private void createComment(
            List<UserEntity> users,
            List<PostEntity> posts
    ) {
        CommentEntity comment = easyRandom.nextObject(CommentEntity.class);
        PostEntity post = posts.get(generateIndex(posts));
        comment.setPost(post);

        Predicate<UserEntity> userIsAuthor = u -> u.getId().equals(post.getAuthor().getId());
        List<UserEntity> commentators = users
                .stream()
                .filter(userIsAuthor)
                .toList();
        if (commentators.isEmpty()) commentators = users;
        UserEntity user = commentators.get(generateIndex(commentators));
        comment.setId(null);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        post.getComments().add(comment);
        log.debug("Generate Comment:{}", comment);
    }

    private int generateIndex(List<?> posts) {
        return easyRandom.nextInt(posts.size());
    }

    private List<UserEntity> initializeUsers() {
        return IntStream.range(0, USER_COUNT).mapToObj(this::createUser).toList();
    }

    private List<PostEntity> initializePosts(List<UserEntity> users) {
        final List<PostEntity> posts = new ArrayList<>();
        users.forEach(author -> IntStream.range(0, POSTS_PER_USER)
                .forEach(i -> posts.add(createPost(author, i)))
        );
        return posts;
    }

    private UserEntity createUser(int i) {
        UserEntity user = easyRandom.nextObject(UserEntity.class);
        UserProfileEntity userProfile = easyRandom.nextObject(UserProfileEntity.class);
        user.setId(null);
        userProfile.setId(null);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);
        log.debug("Generate User #{}:{}", i, user);
        return user;
    }

    private PostEntity createPost(UserEntity author, int i) {
        PostEntity post = easyRandom.nextObject(PostEntity.class);
        post.setId(null);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        log.debug("Generate Post #{}:{}", i, post);
        return post;
    }
}
