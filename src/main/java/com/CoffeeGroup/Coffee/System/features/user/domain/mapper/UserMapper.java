package com.CoffeeGroup.Coffee.System.features.user.domain.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity user) {
        return toResponse(user, user.getExternalId());
    }

    public UserResponse toResponse(UserEntity user, UUID externalId) {
        return new UserResponse(externalId, user.emailAddress(), user.isActive(), user.roleName(), user.getCreatedAt());
    }
}
