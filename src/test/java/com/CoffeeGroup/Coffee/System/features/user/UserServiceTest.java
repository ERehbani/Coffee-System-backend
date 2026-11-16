package com.CoffeeGroup.Coffee.System.features.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.Roles;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeService;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IClientService clientService;
    @Mock
    private ICafeService cafeService;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, new UserMapper(), roleRepository, credentialsRepository,
                passwordEncoder, clientService, cafeService);
    }

    @Test
    void registeringAClientCreatesTheUserTheCredentialsAndTheClient() {
        ClientEntity client = ClientEntity.builder().externalId(UUID.randomUUID()).build();
        when(roleRepository.findByRole(Roles.ROLE_CLIENTE)).thenReturn(Optional.of(new RoleEntity(Roles.ROLE_CLIENTE)));
        when(passwordEncoder.encode("Secret123")).thenReturn("hashed");
        when(clientService.create(any(), any())).thenReturn(client);

        UserResponse response = service.save(new CreateAccountRequest("ana@test.com", "Secret123", "Ana"));

        ArgumentCaptor<CredentialsEntity> credentials = ArgumentCaptor.forClass(CredentialsEntity.class);
        verify(credentialsRepository).save(credentials.capture());
        assertThat(credentials.getValue().getUsername()).isEqualTo("ana@test.com");
        assertThat(credentials.getValue().getPassword()).isEqualTo("hashed");
        assertThat(response.externalId()).isEqualTo(client.getExternalId());
        assertThat(response.state()).isTrue();
    }

    @Test
    void anEmailCannotBeRegisteredTwice() {
        when(userRepository.existsByEmail(new Email("ana@test.com"))).thenReturn(true);

        assertThatThrownBy(() -> service.save(new CreateAccountRequest("ana@test.com", "Secret123", "Ana")))
                .isInstanceOf(EntityAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void changingTheEmailAlsoChangesTheLoginUsername() {
        CredentialsEntity credentials = CredentialsEntity.builder().username("old@test.com").build();
        UserEntity user = UserEntity.builder().externalId(UUID.randomUUID()).email(new Email("old@test.com"))
                .active(true).credentials(credentials).build();
        when(userRepository.findByExternalId(user.getExternalId())).thenReturn(Optional.of(user));

        UserResponse response = service.update(user.getExternalId(), new UpdateUserRequest("new@test.com", null));

        assertThat(response.email()).isEqualTo("new@test.com");
        assertThat(credentials.getUsername()).isEqualTo("new@test.com");
    }

    @Test
    void deletingAUserDeactivatesIt() {
        UserEntity user = UserEntity.builder().externalId(UUID.randomUUID()).email(new Email("ana@test.com"))
                .active(true).build();
        when(userRepository.findByExternalId(user.getExternalId())).thenReturn(Optional.of(user));

        service.delete(user.getExternalId());

        assertThat(user.isActive()).isFalse();
    }
}
