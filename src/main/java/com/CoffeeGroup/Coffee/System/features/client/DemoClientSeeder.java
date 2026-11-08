package com.CoffeeGroup.Coffee.System.features.client;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DemoClientSeeder {

    public static final String EMAIL = "cliente@test.com";
    public static final String PASSWORD = "Cliente123";
    public static final String NAME = "Cliente Demo";

    private final CredentialsRepository credentialsRepository;
    private final IUserService userService;

    public void seed() {
        if (credentialsRepository.existsByUsername(EMAIL)) {
            return;
        }
        userService.save(new CreateAccountRequest(EMAIL, PASSWORD, NAME));
    }
}
