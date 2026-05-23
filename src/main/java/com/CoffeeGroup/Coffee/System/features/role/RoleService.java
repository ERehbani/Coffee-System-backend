package com.CoffeeGroup.Coffee.System.features.role;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.CreateRoleRequest;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.RoleResponse;
import com.CoffeeGroup.Coffee.System.features.role.domain.mapper.RoleMapper;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class RoleService implements IRoleService{

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(CreateRoleRequest request) {
        if(roleRepository.existsByName(request.name())){
            throw new EntityAlreadyExistsException("Role already exists");
        }

        RoleEntity role = roleMapper.toEntity(request);
        RoleEntity roleSaved = roleRepository.save(role);


        return roleMapper.toResponse(roleSaved);
    }

    @Override
    public List<RoleResponse> findAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getById(UUID externalId) {
        RoleEntity role = roleRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        return  roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse update(UUID externalId, UpdateUserRequest request) {
        return null;
    } // Dejo return null hasta ahora porque no necesitamos actualizar rol por ahora, luego lo agrego

    @Override
    public void delete(UUID externalId) {
        RoleEntity role = roleRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        roleRepository.delete(role);
    }
}
