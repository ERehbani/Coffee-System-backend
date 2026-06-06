package com.CoffeeGroup.Coffee.System.auth;

import com.CoffeeGroup.Coffee.System.auth.dto.AuthRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.AuthResponse;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;
import com.CoffeeGroup.Coffee.System.features.user.UserService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody CreateAccountRequest newAccountRequest){
        return new ResponseEntity<>(userService.save(newAccountRequest), HttpStatus.CREATED);
    }
}
