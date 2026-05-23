package com.CoffeeGroup.Coffee.System.features.role;

import com.CoffeeGroup.Coffee.System.features.role.domain.dto.CreateRoleRequest;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.RoleResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

public interface IRoleService {
    RoleResponse create(CreateRoleRequest request);
    List<RoleResponse> findAll();
    RoleResponse getById(UUID externalId);
    RoleResponse update(UUID externalId, UpdateUserRequest request);
    void delete(UUID externalId);
}
