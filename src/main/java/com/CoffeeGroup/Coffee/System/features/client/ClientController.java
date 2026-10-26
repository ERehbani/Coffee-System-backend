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
@Tag(name = "Clients", description = "Endpoints for managing client profiles")
public class ClientController {
    private final IClientService clientService;
    private final IClientTierService clientTierService;
    private final IClientDashboardService clientDashboardService;


    //Elimine el endpoint de "crear" ya que se crea automaticamente con usuario, no tiene sentido
    //crear un cliente aislado de un usuario, por lo tanto se le pega al endpount auth/register

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    @Operation(summary = "List all clients", description = "Retrieves all client profiles")
    @ApiResponses
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get client by ID", description = "Retrieves a specific client by its external ID")
    @ApiResponses
    public ResponseEntity<ClientResponse> getById(@Parameter(description = "Client external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(clientService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update client", description = "Updates client profile information")
    @ApiResponses
    public ResponseEntity<ClientResponse> update(@Parameter(description = "Client external ID") @PathVariable UUID externalId, @Valid @RequestBody UpdateClientRequest request
    ) {
        return ResponseEntity.ok(clientService.update(externalId, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete client", description = "Deletes a client by its external ID")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "Client external ID") @PathVariable UUID externalId) {
        clientService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/ViewMyPoints")
    @Operation(summary = "View my points", description = "Retrieves the current user's loyalty points")
    @ApiResponses
    public  ResponseEntity<ClientPointsResponse> viewMyPoints(){
        return ResponseEntity.ok(clientService.viewMyPoints());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/tier")
    @Operation(summary = "View my tier", description = "Retrieves the current client's tier, progress towards the next tier and the benefits enabled at each level")
    @ApiResponses
    public ResponseEntity<ClientTierResponse> viewMyTier() {
        return ResponseEntity.ok(clientTierService.viewMyTier());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/benefits")
    @Operation(summary = "View my tier benefits", description = "Lists the enabled benefits available to the current client's tier and the tiers below it")
    @ApiResponses
    public ResponseEntity<List<TierBenefitResponse>> viewMyBenefits() {
        return ResponseEntity.ok(clientTierService.viewMyBenefits());
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @GetMapping("/me/dashboard")
    @Operation(summary = "View my dashboard", description = "Retrieves the current client's profile, loyalty status, active redemptions, cafes and featured benefits in one response")
    @ApiResponses
    public ResponseEntity<ClientDashboardResponse> viewMyDashboard() {
        return ResponseEntity.ok(clientDashboardService.viewMyDashboard());
    }

}
