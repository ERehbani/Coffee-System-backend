package com.CoffeeGroup.Coffee.System.common.seed;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.Roles;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email}")
    private String email;

    @Value("${app.superadmin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (credentialsRepository.existsByUsername(email)) {
            return;
        }
        UserEntity user = userRepository.save(UserEntity.builder().email(new Email(email)).active(true).build());
        credentialsRepository.save(CredentialsEntity.builder()
                .username(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .user(user)
                .roles(Set.of(roleRepository.findByRole(Roles.ROLE_SUPERADMIN)
                        .orElseThrow(() -> new EntityNotFoundException("Rol ROLE_SUPERADMIN no encontrado"))))
                .build());
    }
}
