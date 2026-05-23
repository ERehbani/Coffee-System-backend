package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
import com.CoffeeGroup.Coffee.System.features.role.RoleRepository;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;


    @Override
    public UserResponse create(CreateUserRequest request) {
        Email email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Email already exists");
        }

        RoleEntity role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        UserEntity userEntity = userMapper.toEntity(request, role);
        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.toResponse(savedUser);
    }


    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper :: toResponse)
                .toList();
    }

    @Override
    public UserResponse getById(UUID externalId) {
        UserEntity user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return  userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(UUID externalId, UpdateUserRequest request) {
        UserEntity user  = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(request.email() != null ){
            user.setEmail(new Email(request.email()));
        }

        if(request.password() != null ){
            user.setPassword(request.password());
        }

        if (request.state() != null){
            user.setState(request.state());
        }

        if(request.roleId() != null){
            RoleEntity role = roleRepository.findById(request.roleId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            user.setRole(role);
        }

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UUID externalId) {
        UserEntity user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);
    }


}
