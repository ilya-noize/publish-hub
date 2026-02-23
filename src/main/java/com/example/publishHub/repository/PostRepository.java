package com.example.publishHub.repository;

import com.example.publishHub.entity.PostEntity;
import com.example.publishHub.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    // - Найти посты по автору
    List<PostEntity> findAllByAuthor(UserEntity user);
    // - Найти посты по автору
    List<PostEntity> findAllByAuthor_Id(Long id);

    // - Найти посты с комментариями (используйте @EntityGraph)
    @EntityGraph(attributePaths = {"comments"})
    List<PostEntity> findAllByComments_ApprovedNot(boolean approved);;

    // - Найти посты созданные после указанной даты
    List<PostEntity> findAllByCreatedAtAfter(LocalDateTime createdAt);

    PostEntity findByAuthor_IdAndId(Long id, Long id1);
}