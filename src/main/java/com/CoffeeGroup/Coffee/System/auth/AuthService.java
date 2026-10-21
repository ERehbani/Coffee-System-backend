package com.CoffeeGroup.Coffee.System.auth;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.dto.AuthRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.MeResponse;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
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

    @Transactional(readOnly = true)
    public MeResponse me(String username) {
        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String role = credentials.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        UserEntity user = credentials.getUsuario();
        ClientEntity client = user != null ? user.getClient() : null;
        CafeEntity cafe = user != null ? user.getCafe() : null;

        String name = client != null ? client.getName() : cafe != null ? cafe.getName() : null;

        return new MeResponse(
                user != null ? user.getExternalId() : null,
                credentials.getUsername(),
                role,
                name,
                client != null ? client.getExternalId() : null,
                cafe != null ? cafe.getExternalId() : null
        );
    }
}