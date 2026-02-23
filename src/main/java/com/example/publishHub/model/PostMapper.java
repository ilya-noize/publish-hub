package com.example.publishHub.model;

import com.example.publishHub.entity.PostEntity;
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
public interface PostMapper {

    @Mapping(target = "createdAt", ignore = true)
    PostEntity toEntity(PostDto dto);

    PostDto toDomain(PostEntity entity);

    @Mapping(target = "author.id", source = "authorId")
    PostDto toDomain(PostRequest request);

    PostResponse toResponse(PostDto dto);
}
