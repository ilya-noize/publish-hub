package com.example.publishHub.repository;

import com.example.publishHub.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // - Найти комментарии поста
    List<CommentEntity> findAllByPost_Id(Long id);

    // - Найти неподтвержденные комментарии
    List<CommentEntity> findAllByApprovedNot(boolean approved);

    // - Найти комментарии по автору
    List<CommentEntity> findAllByUser_Id(Long userId);

    // - Удалить все комментарии поста
    int deleteByPost_Id(Long postId);

    CommentEntity findByIdAndPost_Id(Long id, Long id1);

    CommentEntity findByIdAndPost_IdAndApprovedNot(Long id, Long id1, boolean approved);
}