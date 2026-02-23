package com.example.publishHub.model;

import com.example.publishHub.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        uses = {UserMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserProfileMapper {

    @Mapping(target = "user.id", source = "id")
    UserProfileEntity toEntity(UserProfileDto dto);

    @Mapping(target = "userId", source = "user.id")
    UserProfileDto toDomain(UserProfileEntity entity);
}
