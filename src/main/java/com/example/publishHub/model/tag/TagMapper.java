package com.example.publishHub.model.tag;

import com.example.publishHub.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {

    TagEntity toEntity(TagDto dto);

    @Mapping(target = "id", ignore = true)
    TagDto toDomain(TagPostRequest request);

    TagDto toDomain(TagPutRequest request);

    TagDto toDomain(TagEntity entity);

    TagResponse toResponse(TagDto dto);
}
