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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tiers")
@RequiredArgsConstructor
@Tag(name = "Tiers", description = "Endpoints for managing loyalty tiers")
public class TierController {
    private final ITierService tierService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    @Operation(summary = "List all tiers", description = "Retrieves all loyalty tiers")
    @ApiResponses
    public ResponseEntity<List<TierResponse>> getAll() {
        return ResponseEntity.ok(tierService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get tier by ID", description = "Retrieves a specific tier by its external ID")
    @ApiResponses
    public ResponseEntity<TierResponse> getById(@Parameter(description = "Tier external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(tierService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Create tier", description = "Creates a new loyalty tier")
    @ApiResponses
    public ResponseEntity<TierResponse> create(@Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update tier", description = "Updates tier information")
    @ApiResponses
    public ResponseEntity<TierResponse> update(
            @Parameter(description = "Tier external ID") @PathVariable UUID externalId,
            @Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete tier", description = "Deletes a tier by its external ID")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "Tier external ID") @PathVariable UUID externalId) {
        tierService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
