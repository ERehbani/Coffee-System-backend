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
@Tag(name = "Compras", description = "Endpoints para administrar compras")
public class PurchaseController {

    private final IPurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Crear compra", description = "Compra el carrito, opcionalmente con un código de canje")
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Listar compras", description = "Paginado; cada rol solo ve lo que le corresponde")
    public ResponseEntity<PageResponse<PurchaseResponse>> findAll(
            @Parameter(description = "Filtra por estado de la compra, varios separados por comas")
            @RequestParam(required = false) List<PurchaseStatus> status,
            @Parameter(description = "Desde la fecha (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Hasta la fecha (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.findAll(new PurchaseFilter(status, from, to), pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Obtener mi historial de compras")
    public ResponseEntity<PageResponse<PurchaseResponse>> getMyHistory(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getMyHistory(pageable));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Obtener compra por ID")
    public ResponseEntity<PurchaseResponse> getById(
            @Parameter(description = "ID externo de la compra") @PathVariable UUID externalId) {
        return ResponseEntity.ok(purchaseService.getById(externalId));
    }

    @PatchMapping("/{externalId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Cambiar el estado de una compra",
            description = "Avanza el pedido por IN_PREPARATION, READY y DELIVERED. Solo CANCELLED o REFUNDED "
                    + "retiran los puntos que otorgó, reactivan el cupón y avisan al cliente por email")
    public ResponseEntity<PurchaseResponse> updateStatus(
            @Parameter(description = "ID externo de la compra") @PathVariable UUID externalId,
            @Valid @RequestBody UpdatePurchaseStatusRequest request) {
        return ResponseEntity.ok(purchaseService.updateStatus(externalId, request));
    }

    @GetMapping("/cafe/{cafeId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Historial de compras de una cafetería")
    public ResponseEntity<PageResponse<PurchaseResponse>> getHistoryByCafe(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "Filtra por estado de la compra, varios separados por comas")
            @RequestParam(required = false) List<PurchaseStatus> status,
            @Parameter(description = "Desde la fecha (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Hasta la fecha (yyyy-MM-dd)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getHistoryByCafe(cafeId, new PurchaseFilter(status, from, to), pageable));
    }

    @GetMapping("/client/{clientId}/history")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @Operation(summary = "Historial de compras de un cliente")
    public ResponseEntity<PageResponse<PurchaseResponse>> getHistoryByClient(
            @Parameter(description = "ID externo del cliente") @PathVariable UUID clientId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getHistoryByClient(clientId, pageable));
    }
}
