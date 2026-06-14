package com.CoffeeGroup.Coffee.System.features.tier;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tiers")
@RequiredArgsConstructor
public class TierController {
    private final ITierService tierService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    public ResponseEntity<List<TierResponse>> getAll() {
        return ResponseEntity.ok(tierService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{externalId}")
    public ResponseEntity<TierResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(tierService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    public ResponseEntity<TierResponse> create(@Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    public ResponseEntity<TierResponse> update(
            @PathVariable UUID externalId,
            @Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        tierService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
