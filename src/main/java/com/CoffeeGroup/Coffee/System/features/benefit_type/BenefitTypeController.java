package com.CoffeeGroup.Coffee.System.features.benefit_type;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/benefittypes")
@Tag(name = "Benefit Types", description = "Endpoints for managing benefit types")
public class BenefitTypeController {
    private final  BenefitTypeService benefitTypeService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @Operation(summary = "Create benefit type", description = "Creates a new benefit type")
    @ApiResponses
    ResponseEntity<BenefitTypeResponse> create(@Valid @RequestBody BenefitTypeCreate request) {
        return new ResponseEntity<>(benefitTypeService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "List all benefit types", description = "Retrieves all benefit types")
    @ApiResponses
    ResponseEntity<List<BenefitTypeResponse>> allTypeBenefit(){
        return ResponseEntity.ok(benefitTypeService.getAll());
    }
}
