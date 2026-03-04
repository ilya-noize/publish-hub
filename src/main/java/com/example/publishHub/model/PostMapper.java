package com.example.publishHub.model;

import com.example.publishHub.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        uses = {
                UserMapper.class,
                CommentMapper.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PostMapper {

    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "createdAt", ignore = true)
    PostEntity toEntity(PostDto dto);

    @Mapping(target = "authorId", source = "author.id")
    PostDto toDomain(PostEntity entity);

    @Mapping(target = "authorId", source = "authorId")
    PostDto toDomain(PostRequest request);

    @Mapping(target = "author.id", source = "authorId")
    PostResponse toResponse(PostDto dto);
}
