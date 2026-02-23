package com.example.publishHub.model;

import com.example.publishHub.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(
        uses = {
                UserMapper.class,
                PostMapper.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CommentMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "approved", ignore = true)
    CommentEntity toEntity(CommentDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "postId", source = "post.id")
    CommentDto toDomain(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    CommentDto toDomain(CommentRequest request);

    CommentResponse toResponse(CommentDto dto);
}
