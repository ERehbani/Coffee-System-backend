package com.CoffeeGroup.Coffee.System.features.purchase;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> findAll() {
        return ResponseEntity.ok(purchaseService.findAll());
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(purchaseService.getById(externalId));
    }

    @PatchMapping("/{externalId}/status")
    public ResponseEntity<PurchaseResponse> updateStatus(@PathVariable UUID externalId, @Valid @RequestBody UpdatePurchaseStatusRequest request
    ) {
        return ResponseEntity.ok(purchaseService.updateStatus(externalId, request));
    }


    //el traductor del orto me traduce "historial" a "record", pense que era history la traduccion
    @GetMapping("/cafes/{cafeId}/record")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    public ResponseEntity<List<PurchaseResponse>> getHistorialByCafe(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        return ResponseEntity.ok(purchaseService.getHistorialByCafe(cafeId, status, from, to));
    }
}
