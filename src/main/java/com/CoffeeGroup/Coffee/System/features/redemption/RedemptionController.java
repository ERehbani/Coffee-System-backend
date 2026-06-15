package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/redemption")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest redemptionCreateRequest) {
        return new ResponseEntity<>(redemptionService.create(redemptionCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    public ResponseEntity<List<RedemptionResponse>> getAll(
            @RequestParam(required = false) Boolean state) {
        return ResponseEntity.ok(redemptionService.findAll(state));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    public ResponseEntity<RedemptionResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA','ROLE_SUPERADMIN')")
    @PostMapping("/validate/{validationCode}")
    ResponseEntity<RedemptionResponse> confirmRedemption(@PathVariable String validationCode) {
        return ResponseEntity.ok(redemptionService.validate(validationCode));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/client/{clientId}")
    ResponseEntity<List<RedemptionResponse>> getAllByClient(@PathVariable UUID clientId){
        return ResponseEntity.ok(redemptionService.getAllByClient(clientId));
    }
}
