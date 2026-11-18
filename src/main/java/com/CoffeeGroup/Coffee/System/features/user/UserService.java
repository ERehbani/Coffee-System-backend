package com.CoffeeGroup.Coffee.System.features.user;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeService;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final IClientService clientService;
    private final ICafeService cafeService;

    @Override
    public PageResponse<UserResponse> getAll(Pageable pageable) {
        return PageResponse.of(userRepository.findAll(pageable), userMapper::toResponse);
    }

    @Override
    public UserResponse getById(UUID externalId) {
        return userMapper.toResponse(findUser(externalId));
    }

    @Override
    @Transactional
    public UserResponse save(CreateAccountRequest request) {
        UserEntity user = createAccount(request.email(), request.password(), Roles.ROLE_CLIENTE);
        ClientEntity client = clientService.create(request.name(), user);
        return userMapper.toResponse(user, client.getExternalId());
    }

    @Override
    @Transactional
    public UserResponse saveCafe(CreateCafeAccountRequest request) {
        UserEntity user = createAccount(request.email(), request.password(), Roles.ROLE_CAFETERIA);
        CafeResponse cafe = cafeService.create(request, user);
        return userMapper.toResponse(user, cafe.externalId());
    }

    @Override
    @Transactional
    public UserResponse update(UUID externalId, UpdateUserRequest request) {
        UserEntity user = findUser(externalId);
        if (request.email() != null && !request.email().equalsIgnoreCase(user.emailAddress())) {
            changeEmail(user, request.email());
        }
        if (request.state() != null) {
            user.setActive(request.state());
        }
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        findUser(externalId).deactivate();
    }

    private UserEntity createAccount(String emailAddress, String password, Roles roleName) {
        Email email = new Email(emailAddress);
        ensureEmailIsFree(email);
        RoleEntity role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Rol " + roleName + " no encontrado"));
        UserEntity user = UserEntity.builder().email(email).active(true).build();
        userRepository.save(user);
        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(emailAddress)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .user(user)
                .roles(Set.of(role))
                .build();
        credentialsRepository.save(credentials);
        // Keep the inverse side in sync so the response can read the role in this same transaction.
        user.setCredentials(credentials);
        return user;
    }

    private void changeEmail(UserEntity user, String newAddress) {
        Email email = new Email(newAddress);
        ensureEmailIsFree(email);
        user.setEmail(email);
        CredentialsEntity credentials = user.getCredentials();
        if (credentials != null) {
            credentials.setUsername(newAddress);
        }
    }

    private void ensureEmailIsFree(Email email) {
        if (userRepository.existsByEmail(email) || credentialsRepository.existsByUsername(email.value())) {
            throw new EntityAlreadyExistsException("Ya existe una cuenta con ese email");
        }
    }

    private UserEntity findUser(UUID externalId) {
        return userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }
}
