package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para administrar perfiles de clientes")
public class ClientController {
    private final IClientService clientService;
    private final IClientTierService clientTierService;
    private final IClientDashboardService clientDashboardService;



    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    @Operation(summary = "Listar todos los clientes", description = "Obtiene todos los perfiles de clientes")
    @ApiResponses
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente por su ID externo")
    @ApiResponses
    public ResponseEntity<ClientResponse> getById(@Parameter(description = "ID externo del cliente") @PathVariable UUID externalId) {
        return ResponseEntity.ok(clientService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos del perfil del cliente")
    @ApiResponses
    public ResponseEntity<ClientResponse> update(@Parameter(description = "ID externo del cliente") @PathVariable UUID externalId, @Valid @RequestBody UpdateClientRequest request
    ) {
        return ResponseEntity.ok(clientService.update(externalId, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su ID externo")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo del cliente") @PathVariable UUID externalId) {
        clientService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/ViewMyPoints")
    @Operation(summary = "Ver mis puntos", description = "Obtiene los puntos de fidelidad del usuario actual")
    @ApiResponses
    public  ResponseEntity<ClientPointsResponse> viewMyPoints(){
        return ResponseEntity.ok(clientService.viewMyPoints());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/tier")
    @Operation(summary = "Ver mi tier", description = "Obtiene el tier del cliente actual, su avance hacia el siguiente tier y los beneficios habilitados en cada tier")
    @ApiResponses
    public ResponseEntity<ClientTierResponse> viewMyTier() {
        return ResponseEntity.ok(clientTierService.viewMyTier());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/benefits")
    @Operation(summary = "Ver los beneficios de mi tier", description = "Lista los beneficios habilitados para el tier del cliente actual y los tiers inferiores")
    @ApiResponses
    public ResponseEntity<List<TierBenefitResponse>> viewMyBenefits() {
        return ResponseEntity.ok(clientTierService.viewMyBenefits());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/dashboard")
    @Operation(summary = "Ver mi dashboard", description = "Obtiene en una sola respuesta el perfil del cliente actual, su estado de fidelidad, los canjes activos, las cafeterías y los beneficios destacados")
    @ApiResponses
    public ResponseEntity<ClientDashboardResponse> viewMyDashboard() {
        return ResponseEntity.ok(clientDashboardService.viewMyDashboard());
    }

}
