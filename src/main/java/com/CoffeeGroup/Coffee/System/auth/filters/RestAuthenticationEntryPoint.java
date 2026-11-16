package com.CoffeeGroup.Coffee.System.auth.filters;

import java.io.IOException;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ProblemResponseWriter problemResponseWriter;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        problemResponseWriter.writeUnauthorized(request, response, messageFor(exception));
    }

    private static String messageFor(AuthenticationException exception) {
        return switch (exception) {
            case BadCredentialsException ignored -> "Credenciales inválidas";
            case DisabledException ignored -> "Cuenta deshabilitada";
            case LockedException ignored -> "Cuenta bloqueada";
            case AccountExpiredException ignored -> "Cuenta expirada";
            case CredentialsExpiredException ignored -> "Credenciales expiradas";
            case InsufficientAuthenticationException ignored -> "Autenticación insuficiente";
            case AuthenticationServiceException ignored -> "Error en el servicio de autenticación";
            default -> "Error de autenticación: " + exception.getMessage();
        };
    }
}
