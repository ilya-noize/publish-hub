package com.example.publishHub.model.user;

import com.example.publishHub.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        uses = {UserProfileMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {

    UserEntity toEntity(UserDto dto);

    UserDto toDomain(UserEntity entity);
}
