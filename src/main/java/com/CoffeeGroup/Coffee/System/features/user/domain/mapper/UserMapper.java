package com.CoffeeGroup.Coffee.System.features.user.domain.mapper;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

@Component
public class UserMapper {
    public UserEntity toEntity(CreateUserRequest request) {
        return UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(new Email(request.email()))
                .state(true)
                .creationDate(new Date())
                .build();
    }

    public UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getExternalId(),
                user.getEmail().value(),
                user.getState()
        );
    }
}
