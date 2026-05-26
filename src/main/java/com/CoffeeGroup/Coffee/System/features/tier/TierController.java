package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponseDTO;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<TierResponseDTO>> getAll() {
        return ResponseEntity.ok(tierService.getAll());
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<TierResponseDTO> findById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(tierService.getById(externalId));
    }

    @PostMapping
    public ResponseEntity<TierResponseDTO> create(@Valid @RequestBody NewTierDTO request) {
        return ResponseEntity.ok(tierService.create(request));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<TierResponseDTO> update(
            @PathVariable UUID externalId,
            @Valid @RequestBody NewTierDTO request) {
        return ResponseEntity.ok(tierService.update(externalId, request));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        tierService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
