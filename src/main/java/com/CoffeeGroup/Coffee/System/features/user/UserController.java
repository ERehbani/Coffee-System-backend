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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing user accounts")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves all user accounts")
    @ApiResponses
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by its external ID")
    @ApiResponses
    public ResponseEntity<UserResponse> getById(@Parameter(description = "User external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(userService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update user", description = "Updates user account information")
    @ApiResponses
    public ResponseEntity<UserResponse> update(@Valid @Parameter(description = "User external ID") @PathVariable UUID externalId,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete user", description = "Deletes a user by its external ID")
    @ApiResponses
    public ResponseEntity<UserResponse> delete(@Parameter(description = "User external ID") @PathVariable UUID externalId) {
        userService.delete(externalId);

        return ResponseEntity.ok().build();
    }

}
