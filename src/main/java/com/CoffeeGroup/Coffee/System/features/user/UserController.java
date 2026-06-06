package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping ("/{externalId}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(userService.getById(externalId));
    }


    @PutMapping("/{externalId}")
    public ResponseEntity<UserResponse> update(@Valid @PathVariable UUID externalId, @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.update(externalId, request));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<UserResponse> delete(@PathVariable UUID externalId) {
        userService.delete(externalId);

        return ResponseEntity.ok().build();
    }


}
