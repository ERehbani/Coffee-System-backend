package com.CoffeeGroup.Coffee.System.features.role.domain.mapper;

import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.CreateRoleRequest;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RoleMapper {
    public RoleEntity toEntity(CreateRoleRequest role) {
        return RoleEntity.builder()
                .externalId(UUID.randomUUID())
                .name(role.name())
                .build();
    }


    public RoleResponse toResponse(RoleEntity role) {
        return new RoleResponse(role.getExternalId(),
                role.getName());
    }
}
