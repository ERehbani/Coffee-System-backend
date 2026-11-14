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
@Tag(name = "Redemptions", description = "Endpoints for redeeming benefits with points")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Redeem a benefit", description = "Spends the points and returns the validation code")
    public ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest request) {
        return new ResponseEntity<>(redemptionService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "List redemptions", description = "Paginated, newest first; clients only see their own")
    public ResponseEntity<PageResponse<RedemptionResponse>> getAll(
            @Parameter(description = "ACTIVE, USED or CANCELLED") @RequestParam(required = false) RedemptionStatus status,
            @PageableDefault(size = 20, sort = "redeemedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(redemptionService.findAll(status, pageable));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Get redemption by ID")
    public ResponseEntity<RedemptionResponse> getById(
            @Parameter(description = "Redemption external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.getById(externalId));
    }

    @PostMapping("/validate/{validationCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Validate a code", description = "Marks the redemption as used when the cafe delivers it")
    public ResponseEntity<RedemptionResponse> validate(
            @Parameter(description = "Redemption validation code") @PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.validate(validationCode));
    }

    @PostMapping("/cancel/{validationCode}")
    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Cancel a code", description = "Gives the points and the redemption slot back")
    public ResponseEntity<RedemptionResponse> cancel(
            @Parameter(description = "Redemption validation code") @PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.cancel(validationCode));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @Operation(summary = "List the redemptions of a client")
    public ResponseEntity<List<RedemptionResponse>> getAllByClient(
            @Parameter(description = "Client external ID") @PathVariable UUID clientId) {
        return ResponseEntity.ok(redemptionService.getAllByClient(clientId));
    }
}
