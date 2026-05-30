package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper :: toResponse)
                .toList();
    }

    @Transactional
    public UserResponse save(CreateAccountRequest request) {

        Email email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Email already exists");
        }

        RoleEntity role = roleRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        UserEntity user = UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(email)
                .state(true)
                .creationDate(new Date())
                .build();

        UserEntity savedUser = userRepository.save(user);

        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .usuario(savedUser)
                .roles(Set.of(role))
                .build();

        credentialsRepository.save(credentials);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getById(UUID externalId) {
        UserEntity user = findByExternalId(externalId);

        return  userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(UUID externalId, UpdateUserRequest request) {
        UserEntity user = findByExternalId(externalId);

        if(request.email() != null ){
            user.setEmail(new Email(request.email()));
        }

        if (request.state() != null){
            user.setState(request.state());
        }

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UUID externalId) {
        UserEntity user = findByExternalId(externalId);

        userRepository.delete(user);
    }


    public UserEntity findByExternalId(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
