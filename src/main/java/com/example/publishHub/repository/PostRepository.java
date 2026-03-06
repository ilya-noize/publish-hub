package com.example.publishHub.repository;

import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // - Найти пост автора с комментариями (используйте @EntityGraph)
    @EntityGraph(attributePaths = {"comments"})
    Optional<PostEntity> findByIdAndAuthor_Id(Long postId, Long authorId);

    // - Найти посты автора с комментариями (используйте @EntityGraph)
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByAuthor_Id(Long authorId);

    // - Найти посты созданные после указанной даты
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByCreatedAtAfter(LocalDateTime createdAt);

    @Query("""
        SELECT p FROM PostEntity p
        JOIN FETCH p.comments c
        WHERE p.id = :postId
    """)
    @EntityGraph(attributePaths = {"comments"})
    Optional<PostEntity> findByIdWithComments(Long postId);

    // - Найти посты с комментариями и авторами комментариев
    @Query("""
            SELECT DISTINCT p FROM PostEntity p
            JOIN FETCH p.comments c
            JOIN FETCH c.user
            WHERE p.id IN :postIds
            """)
    @EntityGraph(
            attributePaths = {"post-with-comments-and-authors"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    List<PostEntity> findAllByCommentsWithAuthorComments(List<Long> postIds);

    // - Найти посты по тегу с комментариями
    @Query("""
            select distinct p from PostEntity p
            join p.tags t
            join fetch p.comments
            where t.name IN :tags
            """)
    List<PostEntity> findByTagsWithComments(List<String> tags);

    // - Найти популярные посты с количеством комментариев
    @Query("""
            select p.id id, p.title title, count(c) as commentCount from PostEntity p
            join p.comments c
            group by p.id, p.title
            order by commentCount desc
            """)
    List<PostWithCommentCountProjection> findPopularPosts();

    // TODO: Создайте методы с EntityGraph:
    // - Все посты с тегами
    @EntityGraph(attributePaths = {"tags"})
    List<PostEntity> findAllByTags_IdIn(List<Long> tagIds);

    // - Посты пользователя с комментариями
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByAuthor_IdOrderByCreatedAtDescId(Long authorId);

    @Query("""
        SELECT DISTINCT p FROM PostEntity p
        LEFT JOIN FETCH p.comments
        WHERE p.author.id = :authorId
        """)
    List<PostEntity> findByAuthor_IdWithComments(Long authorId);

    // - Посты за период с основной информацией
    @EntityGraph(
            attributePaths = {"author"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    List<PostEntity> findByCreatedAtBetween(
            LocalDateTime createdAtStart,
            LocalDateTime createdAtEnd
    );

    boolean existsByIdAndAuthor_Id(Long id, Long id1);
}