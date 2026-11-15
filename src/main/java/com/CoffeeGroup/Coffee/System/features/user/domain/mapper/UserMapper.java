package com.CoffeeGroup.Coffee.System.features.user.domain.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity user) {
        return toResponse(user, user.getExternalId());
    }

    public UserResponse toResponse(UserEntity user, UUID externalId) {
        return new UserResponse(externalId, user.emailAddress(), user.isActive(), role(user), user.getCreatedAt());
    }

    // Every account is created with exactly one role, so the first one is the user's role.
    private String role(UserEntity user) {
        CredentialsEntity credentials = user.getCredentials();
        if (credentials == null) {
            return null;
        }
        return credentials.getRoles().stream()
                .findFirst()
                .map(role -> role.getRole().name())
                .orElse(null);
    }
}
