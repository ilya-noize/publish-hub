package com.example.publishHub.repository;

import com.example.publishHub.entity.CommentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    /** - Найти комментарии поста */
    @Query("""
        SELECT c FROM CommentEntity c
            WHERE c.post.id = :postId
                 AND c.approved = true
    """)
    @Deprecated
    List<CommentEntity> findAllApprovedByPostId(Long postId);

    /** - Найти все подтвержденные комментарии */
    @Query("""
        SELECT c FROM CommentEntity c
            WHERE c.approved = true
    """)
    @Deprecated
    List<CommentEntity> findAllApproved();

    /** - Найти все не подтвержденные комментарии */
    @Query("""
        SELECT c FROM CommentEntity c
            WHERE c.approved = false
    """)
    @Deprecated
    List<CommentEntity> findAllNotApproved();


    /** - Найти комментарии по автору */
    @Query("""
        SELECT c FROM CommentEntity c
            WHERE c.user.id = :userId
    """)
    List<CommentEntity> findAllByUser_Id(Long userId);

    /** - Удалить все комментарии поста */
    @Modifying
    int deleteByPost_Id(Long postId);

    /** - Найти неподтверждённый комментарий в посте */
    @EntityGraph(attributePaths = {"post"})
    Optional<CommentEntity> findByIdAndPost_IdAndApprovedNot(Long commentId, Long postId, boolean isApproved);

    int deleteByIdAndPost_IdAndUser_Id(Long commentId, Long postId, Long userId);

    boolean existsByIdAndUser_IdAndPost_Id(Long commentId, Long postId, Long userId);

    Optional<CommentEntity> findByIdAndUser_IdAndPost_Id(Long id, Long id1, Long id2);
}