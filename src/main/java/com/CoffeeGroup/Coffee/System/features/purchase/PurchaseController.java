package com.CoffeeGroup.Coffee.System.features.purchase;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
@Tag(name = "Purchases", description = "Endpoints for managing purchases")
public class PurchaseController {

    private final IPurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Create purchase", description = "Creates a new purchase from the user's cart")
    @ApiResponses
    ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "List all purchases", description = "Retrieves all purchases with optional filters")
    @ApiResponses
    ResponseEntity<List<PurchaseResponse>> findAll(
            @Parameter(description = "Filter by purchase status") @RequestParam(required = false) PurchaseStatus status,
            @Parameter(description = "Filter by start date") @RequestParam(required = false) Date from,
            @Parameter(description = "Filter by end date") @RequestParam(required = false) Date to) {
        return ResponseEntity.ok(purchaseService.findAll(status, from, to));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Get purchase by ID", description = "Retrieves a specific purchase by its external ID")
    @ApiResponses
    ResponseEntity<PurchaseResponse> getById(@Parameter(description = "Purchase external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(purchaseService.getById(externalId));
    }


    @GetMapping("/cafe/{cafeId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Get cafe purchase history", description = "Retrieves purchase history for a specific cafeteria")
    @ApiResponses
    ResponseEntity<List<PurchaseResponse>> getHistorialByCafe(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Filter by purchase status") @RequestParam(required = false) PurchaseStatus status,
            @Parameter(description = "Filter by start date") @RequestParam(required = false) Date from,
            @Parameter(description = "Filter by end date") @RequestParam(required = false) Date to) {
        return ResponseEntity.ok(purchaseService.getHistorialByCafe(cafeId, status, from, to));
    }

    @GetMapping("/client/{clientId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @Operation(summary = "Get client purchase history", description = "Retrieves purchase history for a specific client")
    @ApiResponses
    ResponseEntity<List<PurchaseResponse>> getClientPurchaseHistory(@Parameter(description = "Client external ID") @PathVariable UUID clientId) {
        return ResponseEntity.ok(purchaseService.getHistorialByCliente(clientId));
    }
}
