package com.CoffeeGroup.Coffee.System.features.redemption;

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

import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/redemption")
@Tag(name = "Redemptions", description = "Endpoints for managing benefit redemptions")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    @Operation(summary = "Create redemption", description = "Creates a new benefit redemption")
    @ApiResponses
    ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest redemptionCreateRequest) {
        return new ResponseEntity<>(redemptionService.create(redemptionCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    @Operation(summary = "List all redemptions", description = "Retrieves all redemptions with optional state filter")
    @ApiResponses
    public ResponseEntity<List<RedemptionResponse>> getAll(
            @Parameter(description = "Filter by redemption state") @RequestParam(required = false) Boolean state) {
        return ResponseEntity.ok(redemptionService.findAll(state));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    @Operation(summary = "Get redemption by ID", description = "Retrieves a specific redemption by its external ID")
    @ApiResponses
    public ResponseEntity<RedemptionResponse> getById(@Parameter(description = "Redemption external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA','ROLE_SUPERADMIN')")
    @PostMapping("/validate/{validationCode}")
    @Operation(summary = "Validate redemption", description = "Validates a redemption using its validation code")
    @ApiResponses
    ResponseEntity<RedemptionResponse> confirmRedemption(@Parameter(description = "Redemption validation code") @PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.validate(validationCode));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get client redemptions", description = "Retrieves all redemptions for a specific client")
    @ApiResponses
    ResponseEntity<List<RedemptionResponse>> getAllByClient(@Parameter(description = "Client external ID") @PathVariable UUID clientId){
        return ResponseEntity.ok(redemptionService.getAllByClient(clientId));
    }
}
