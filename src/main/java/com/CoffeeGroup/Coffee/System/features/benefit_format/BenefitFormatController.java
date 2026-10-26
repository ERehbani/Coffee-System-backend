package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/benefit-formats")
@RequiredArgsConstructor
@Tag(name = "Benefit formats", description = "Catalog of the benefit formats admitted on the platform")
public class BenefitFormatController {
    private final IBenefitFormatService benefitFormatService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping
    @Operation(summary = "List benefit formats", description = "Lists the catalog ordered by name, with the benefits using each format")
    public ResponseEntity<List<BenefitFormatResponse>> getAll() {
        return ResponseEntity.ok(benefitFormatService.getAll());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Register benefit format", description = "Adds a format to the catalog")
    public ResponseEntity<BenefitFormatResponse> create(@Valid @RequestBody CreateBenefitFormatRequest request) {
        return ResponseEntity.ok(benefitFormatService.create(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update benefit format", description = "Updates the name, description and availability of a format")
    public ResponseEntity<BenefitFormatResponse> update(
            @Parameter(description = "Benefit format external ID") @PathVariable UUID externalId,
            @Valid @RequestBody UpdateBenefitFormatRequest request) {
        return ResponseEntity.ok(benefitFormatService.update(externalId, request));
    }
}
