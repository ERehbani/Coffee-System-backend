package com.CoffeeGroup.Coffee.System.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.ChangePasswordRequest;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String USERNAME = "cliente@test.com";
    private static final String CURRENT_PASSWORD = "Actual123";

    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private CredentialsEntity credentials;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        credentials = CredentialsEntity.builder()
                .username(USERNAME)
                .password(passwordEncoder.encode(CURRENT_PASSWORD))
                .enabled(true)
                .build();
        authService = new AuthService(credentialsRepository, authenticationManager, passwordEncoder);
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(credentials));
    }

    @Test
    void changesThePasswordWhenTheCurrentOneIsRight() {
        authService.changePassword(USERNAME, new ChangePasswordRequest(CURRENT_PASSWORD, "Nueva1234"));

        assertThat(passwordEncoder.matches("Nueva1234", credentials.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(CURRENT_PASSWORD, credentials.getPassword())).isFalse();
    }

    @Test
    void rejectsTheChangeWhenTheCurrentPasswordIsWrong() {
        String storedPassword = credentials.getPassword();

        assertThatThrownBy(() -> authService.changePassword(USERNAME,
                new ChangePasswordRequest("Otra1234", "Nueva1234")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("La contraseña actual no es correcta");
        assertThat(credentials.getPassword()).isEqualTo(storedPassword);
    }

    @Test
    void rejectsANewPasswordEqualToTheCurrentOne() {
        assertThatThrownBy(() -> authService.changePassword(USERNAME,
                new ChangePasswordRequest(CURRENT_PASSWORD, CURRENT_PASSWORD)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("La contraseña nueva tiene que ser distinta de la actual");
    }
}
