package com.example.publishHub.model;

import java.util.List;

public record UserDto(
        Long id,
        String name,
        String email,
        UserProfileDto userProfile,
        List<PostDto> posts
        // todo add roles
        //  (USER_ROLE, ADMIN_ROLE, MODERATOR_ROLE)
) {
}
