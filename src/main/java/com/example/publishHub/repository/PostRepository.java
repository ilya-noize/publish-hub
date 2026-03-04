package com.example.publishHub.repository;

import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // - Найти посты по автору
    List<PostEntity> findAllByAuthor(UserEntity user);

    // - Найти посты по автору
//    List<PostEntity> findAllByAuthor_Id(Long id);

    // - Найти посты с комментариями (используйте @EntityGraph)
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByComments_ApprovedNot(boolean approved);

    // - Найти посты созданные после указанной даты
    List<PostEntity> findAllByCreatedAtAfter(LocalDateTime createdAt);

    PostEntity findByAuthor_IdAndId(Long authorId, Long id);

    // TODO: Создайте методы с JOIN FETCH:
    // - Найти посты с комментариями и авторами комментариев
    @Query("""
            select distinct p from PostEntity p
            join fetch p.comments c
            join fetch c.user
            where p.id IN :postIds
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
            where t.id = :tagId
            """)
    List<PostEntity> findByTagWithComments(Long tagId);

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
//    @EntityGraph(attributePaths = {"tags"})
//    List<PostEntity> findAllByTags_IdIn(Collection<Long> tagIds);

    // - Посты пользователя с комментариями
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByAuthor_Id(Long authorId);

//    @Query("""
//        SELECT DISTINCT p FROM PostEntity p
//        LEFT JOIN FETCH p.comments
//        WHERE p.author.id = :authorId
//        """)
//    List<PostEntity> findByAuthor_IdWithComments(Long authorId);

    // - Посты за период с основной информацией
//    @EntityGraph(
//            attributePaths = {"author"},
//            type = EntityGraph.EntityGraphType.FETCH
//    )
//    List<PostEntity> findByCreatedAtBetween(
//            LocalDateTime createdAtStart,
//            LocalDateTime createdAtEnd
//    );
}