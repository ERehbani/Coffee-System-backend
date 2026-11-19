package com.CoffeeGroup.Coffee.System.auth;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.dto.AuthRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.ChangePasswordRequest;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserDetails authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );
        return credentialsRepository.findByUsername(input.email())
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (!passwordEncoder.matches(request.currentPassword(), credentials.getPassword())) {
            throw new BusinessRuleException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(request.newPassword(), credentials.getPassword())) {
            throw new BusinessRuleException("La contraseña nueva tiene que ser distinta de la actual");
        }
        credentials.setPassword(passwordEncoder.encode(request.newPassword()));
    }
}
