package com.CoffeeGroup.Coffee.System.features.purchase;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final IPurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    ResponseEntity<List<PurchaseResponse>> findAll() {
        return ResponseEntity.ok(purchaseService.findAll());
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    ResponseEntity<PurchaseResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(purchaseService.getById(externalId));
    }

    @PatchMapping("/{externalId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    ResponseEntity<PurchaseResponse> updateStatus(
            @PathVariable UUID externalId,
            @Valid @RequestBody UpdatePurchaseStatusRequest request) {
        return ResponseEntity.ok(purchaseService.updateStatus(externalId, request));
    }

    @GetMapping("/cafe/{cafeId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    ResponseEntity<List<PurchaseResponse>> getHistorialByCafe(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) Date from,
            @RequestParam(required = false) Date to) {
        return ResponseEntity.ok(purchaseService.getHistorialByCafe(cafeId, status, from, to));
    }

    @GetMapping("/client/{clientId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    ResponseEntity<List<PurchaseResponse>> getClientPurchaseHistory(@PathVariable UUID clientId) {
        return ResponseEntity.ok(purchaseService.getHistorialByCliente(clientId));
    }
}
