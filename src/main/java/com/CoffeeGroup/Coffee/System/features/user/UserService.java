package com.CoffeeGroup.Coffee.System.features.user;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.Roles;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeService;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.client.ClientService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientService clientService;
    private final CafeService cafeService;

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper :: toResponse)
                .toList();
    }

    //Le saco el @Transactional Ya que el .save es transactional por defecto (si no les gusta se quejan con el profe)
    @Override
    public UserResponse save(CreateAccountRequest request) {

        Email email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Email already exists");
        }

        if (credentialsRepository.existsByUsername(request.email())) {
            throw new EntityAlreadyExistsException("Username already exists");
        }

        RoleEntity role = roleRepository.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new EntityNotFoundException("Role CLIENTE not found"));

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

        var clientEntity = clientService.create(request.name(), savedUser);

        return userMapper.toResponse(savedUser, clientEntity.getExternalId());
    }

    @Override
    public UserResponse saveCafe(CreateCafeAccountRequest request) {

        Email email = new Email(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExistsException("Email already exists");
        }

        if (credentialsRepository.existsByUsername(request.email())) {
            throw new EntityAlreadyExistsException("Username already exists");
        }

        RoleEntity role = roleRepository.findByRole(Roles.ROLE_CAFETERIA)
                .orElseThrow(() -> new EntityNotFoundException("Role cafe not found"));

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

        CafeResponse cafeResponse = cafeService.create(request, savedUser);

        return userMapper.toResponse(savedUser, cafeResponse.externalId());
    }

    @Override
    public UserResponse getById(UUID externalId) {
        UserEntity user = userRepository
                .findByExternalId(externalId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return  userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(UUID externalId, UpdateUserRequest request) {
        UserEntity user = userRepository
                .findByExternalId(externalId).orElseThrow(() -> new EntityNotFoundException("User not found"));

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
        UserEntity user = userRepository
                .findByExternalId(externalId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setState(false);
        userRepository.save(user);
    }



}
