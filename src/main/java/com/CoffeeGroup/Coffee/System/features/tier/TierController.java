package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierDTO;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tiers")
@RequiredArgsConstructor
public class TierController {
    private final ITierService tierService;

    @GetMapping
    public ResponseEntity<List<TierDTO>> findAll() {
        return ResponseEntity.ok(tierService.findAll());
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<TierDTO> findById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(tierService.getById(externalId));
    }

    @PostMapping
    public ResponseEntity<TierDTO> save(
            @Valid @RequestBody NewTierDTO request,
            @RequestHeader(value = "X-User-Role", defaultValue = "SUPER_ADMIN") String roleName,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {

        RoleEntity mockRole = RoleEntity.builder().name(roleName).build();
        UserEntity mockUser = UserEntity.builder().id(userId).role(mockRole).build();

        return ResponseEntity.ok(tierService.create(request, mockUser));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<TierDTO> update(
            @PathVariable UUID externalId,
            @Valid @RequestBody NewTierDTO request,
            @RequestHeader(value = "X-User-Role", defaultValue = "SUPER_ADMIN") String roleName,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {

        RoleEntity mockRole = RoleEntity.builder().name(roleName).build();
        UserEntity mockUser = UserEntity.builder().id(userId).role(mockRole).build();

        return ResponseEntity.ok(tierService.update(externalId, request, mockUser));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID externalId,
            @RequestHeader(value = "X-User-Role", defaultValue = "SUPER_ADMIN") String roleName,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {

        RoleEntity mockRole = RoleEntity.builder().name(roleName).build();
        UserEntity mockUser = UserEntity.builder().id(userId).role(mockRole).build();

        tierService.delete(externalId, mockUser);
        return ResponseEntity.ok().build();
    }
}
