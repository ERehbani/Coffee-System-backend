package com.CoffeeGroup.Coffee.System.auth;

import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.dto.AuthRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.SessionResponse;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;
import com.CoffeeGroup.Coffee.System.features.user.UserService;
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
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthCookieService authCookieService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user with email and password and sets the session cookie")
    @ApiResponses
    public ResponseEntity<SessionResponse> authenticateUser(@RequestBody @Valid AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        ResponseCookie cookie = authCookieService.createSessionCookie(jwtService.generateToken(user));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(SessionResponse.of(user.getUsername(), user.getAuthorities()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "Removes the session cookie")
    @ApiResponses
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.createExpiredCookie().toString())
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Current session", description = "Returns the email and roles of the authenticated user")
    @ApiResponses
    public ResponseEntity<SessionResponse> currentSession(Authentication authentication) {
        return ResponseEntity.ok(SessionResponse.of(authentication.getName(), authentication.getAuthorities()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account with client profile")
    @ApiResponses
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid CreateAccountRequest newAccountRequest){
        return new ResponseEntity<>(userService.save(newAccountRequest), HttpStatus.CREATED);
    }
}
