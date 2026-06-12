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
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest redemptionCreateRequest){
        return new ResponseEntity<>(redemptionService.create(redemptionCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    public ResponseEntity<List<RedemptionResponse>> getAll(@PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.findByClients(externalId));
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENTE','ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        redemptionService.deleteById(externalId);
        return ResponseEntity.noContent().build();
    }



}
