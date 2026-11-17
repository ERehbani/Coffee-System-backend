package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/redemption")
@RequiredArgsConstructor
@Tag(name = "Canjes", description = "Endpoints para canjear beneficios con puntos")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Canjear un beneficio", description = "Descuenta los puntos y devuelve el código de validación")
    public ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest request) {
        return new ResponseEntity<>(redemptionService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Listar canjes", description = "Paginado, los más nuevos primero; los clientes solo ven los suyos")
    public ResponseEntity<PageResponse<RedemptionResponse>> getAll(
            @Parameter(description = "ACTIVE, USED, CANCELLED o EXPIRED") @RequestParam(required = false) RedemptionStatus status,
            @PageableDefault(size = 20, sort = "redeemedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(redemptionService.findAll(status, pageable));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Obtener canje por ID")
    public ResponseEntity<RedemptionResponse> getById(
            @Parameter(description = "ID externo del canje") @PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.getById(externalId));
    }

    @PostMapping("/validate/{validationCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Validar un código", description = "Marca el canje como usado cuando la cafetería lo entrega")
    public ResponseEntity<RedemptionResponse> validate(
            @Parameter(description = "Código de validación del canje") @PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.validate(validationCode));
    }

    @PostMapping("/cancel/{validationCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Cancelar un código", description = "Devuelve los puntos y el cupo del canje")
    public ResponseEntity<RedemptionResponse> cancel(
            @Parameter(description = "Código de validación del canje") @PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.cancel(validationCode));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @Operation(summary = "Listar los canjes de un cliente")
    public ResponseEntity<List<RedemptionResponse>> getAllByClient(
            @Parameter(description = "ID externo del cliente") @PathVariable UUID clientId) {
        return ResponseEntity.ok(redemptionService.getAllByClient(clientId));
    }
}
