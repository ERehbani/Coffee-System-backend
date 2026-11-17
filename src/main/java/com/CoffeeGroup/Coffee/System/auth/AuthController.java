package com.CoffeeGroup.Coffee.System.auth;

import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.dto.AuthRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.SessionResponse;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticar y registrar usuarios")
public class AuthController {
    private final AuthService authService;
    private final IUserService userService;
    private final JwtService jwtService;
    private final AuthCookieService authCookieService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Autentica al usuario con email y contraseña y guarda la cookie de sesión")
    @ApiResponses
    public ResponseEntity<SessionResponse> authenticateUser(@RequestBody @Valid AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        ResponseCookie cookie = authCookieService.createSessionCookie(jwtService.generateToken(user));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SessionResponse.of(user.getUsername(), user.getAuthorities()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Elimina la cookie de sesión")
    @ApiResponses
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.createExpiredCookie().toString())
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Sesión actual", description = "Devuelve el email y los roles del usuario autenticado")
    @ApiResponses
    public ResponseEntity<SessionResponse> currentSession(Authentication authentication) {
        return ResponseEntity.ok(SessionResponse.of(authentication.getName(), authentication.getAuthorities()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario nuevo", description = "Crea una cuenta de usuario con perfil de cliente")
    @ApiResponses
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid CreateAccountRequest newAccountRequest){
        return new ResponseEntity<>(userService.save(newAccountRequest), HttpStatus.CREATED);
    }
}
