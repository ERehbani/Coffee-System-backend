package com.CoffeeGroup.Coffee.System.features.purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseFilter;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchases")
@Tag(name = "Purchases", description = "Endpoints for managing purchases")
public class PurchaseController {

    private final IPurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Create purchase", description = "Buys the cart, optionally with a redemption code")
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "List purchases", description = "Paginated; each role only sees what belongs to it")
    public ResponseEntity<PageResponse<PurchaseResponse>> findAll(
            @Parameter(description = "Filter by purchase status, several separated by commas")
            @RequestParam(required = false) List<PurchaseStatus> status,
            @Parameter(description = "From date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.findAll(new PurchaseFilter(status, from, to), pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Get my purchase history")
    public ResponseEntity<PageResponse<PurchaseResponse>> getMyHistory(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getMyHistory(pageable));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Get purchase by ID")
    public ResponseEntity<PurchaseResponse> getById(
            @Parameter(description = "Purchase external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(purchaseService.getById(externalId));
    }

    @PatchMapping("/{externalId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Change the status of a purchase",
            description = "Moves the order through IN_PREPARATION, READY and DELIVERED. Only CANCELLED or REFUNDED "
                    + "takes back the points it gave, reactivates the coupon and emails the client")
    public ResponseEntity<PurchaseResponse> updateStatus(
            @Parameter(description = "Purchase external ID") @PathVariable UUID externalId,
            @Valid @RequestBody UpdatePurchaseStatusRequest request) {
        return ResponseEntity.ok(purchaseService.updateStatus(externalId, request));
    }

    @GetMapping("/cafe/{cafeId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Purchase history of a cafe")
    public ResponseEntity<PageResponse<PurchaseResponse>> getHistoryByCafe(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Filter by purchase status, several separated by commas")
            @RequestParam(required = false) List<PurchaseStatus> status,
            @Parameter(description = "From date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "To date (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getHistoryByCafe(cafeId, new PurchaseFilter(status, from, to), pageable));
    }

    @GetMapping("/client/{clientId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @Operation(summary = "Purchase history of a client")
    public ResponseEntity<PageResponse<PurchaseResponse>> getHistoryByClient(
            @Parameter(description = "Client external ID") @PathVariable UUID clientId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getHistoryByClient(clientId, pageable));
    }
}
