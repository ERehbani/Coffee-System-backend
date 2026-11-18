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
@Tag(name = "Tiers", description = "Endpoints para administrar los tiers de fidelidad")
public class TierController {
    private final ITierService tierService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping
    @Operation(summary = "Listar todos los tiers", description = "Obtiene todos los tiers de fidelidad")
    @ApiResponses
    public ResponseEntity<List<TierResponse>> getAll() {
        return ResponseEntity.ok(tierService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener tier por ID", description = "Obtiene un tier por su ID externo")
    @ApiResponses
    public ResponseEntity<TierResponse> getById(@Parameter(description = "ID externo del tier") @PathVariable UUID externalId) {
        return ResponseEntity.ok(tierService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Crear tier", description = "Crea un tier de fidelidad nuevo")
    @ApiResponses
    public ResponseEntity<TierResponse> create(@Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.create(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar tier", description = "Actualiza los datos de un tier")
    @ApiResponses
    public ResponseEntity<TierResponse> update(
            @Parameter(description = "ID externo del tier") @PathVariable UUID externalId,
            @Valid @RequestBody CreateTierRequest request) {
        return ResponseEntity.ok(tierService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar tier", description = "Elimina un tier por su ID externo")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo del tier") @PathVariable UUID externalId) {
        tierService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
