package com.CoffeeGroup.Coffee.System.features.user;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{externalId}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(userService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    public ResponseEntity<UserResponse> update(@Valid @PathVariable UUID externalId,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    public ResponseEntity<UserResponse> delete(@PathVariable UUID externalId) {
        userService.delete(externalId);

        return ResponseEntity.ok().build();
    }

}
