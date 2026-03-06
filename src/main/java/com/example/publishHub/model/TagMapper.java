package com.example.publishHub.model;

import com.example.publishHub.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {

    TagEntity toEntity(TagDto dto);

    TagDto toDomain(TagEntity entity);

    TagResponse toResponse(TagDto dto);
}
