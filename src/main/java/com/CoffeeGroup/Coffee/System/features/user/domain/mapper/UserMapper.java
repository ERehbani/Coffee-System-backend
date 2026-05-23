package com.CoffeeGroup.Coffee.System.features.user.domain.mapper;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class UserMapper {
    public UserEntity toEntity (CreateUserRequest request, RoleEntity role) {
        return UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(new Email(request.email()))
                .password(request.password())
                .state(true)
                .creationDate(new Date())
                .role(role)
                .build();
    }

    public UserResponse toResponse (UserEntity user) {
        return new UserResponse(user.getExternalId(),
                user.getEmail().value(),
                user.getState(),
                user.getRole().getName());
    }
}
