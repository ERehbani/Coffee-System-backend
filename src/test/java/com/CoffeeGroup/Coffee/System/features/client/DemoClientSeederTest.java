package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class DemoClientSeederTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @Mock
    private IUserService userService;

    private DemoClientSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new DemoClientSeeder(credentialsRepository, userService);
    }

    @Test
    void createsTheDemoClientWhenItDoesNotExist() {
        when(credentialsRepository.existsByUsername(DemoClientSeeder.EMAIL)).thenReturn(false);

        seeder.seed();

        ArgumentCaptor<CreateAccountRequest> captor = ArgumentCaptor.forClass(CreateAccountRequest.class);
        verify(userService).save(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("cliente@test.com");
        assertThat(captor.getValue().password()).isEqualTo("Cliente123");
        assertThat(captor.getValue().name()).isEqualTo("Cliente Demo");
    }

    @Test
    void doesNothingWhenTheDemoClientAlreadyExists() {
        when(credentialsRepository.existsByUsername(DemoClientSeeder.EMAIL)).thenReturn(true);

        seeder.seed();

        verify(userService, never()).save(any());
    }

    @Test
    void usesCredentialsThatPassTheRegisterValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CreateAccountRequest request = new CreateAccountRequest(
                DemoClientSeeder.EMAIL, DemoClientSeeder.PASSWORD, DemoClientSeeder.NAME);

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
