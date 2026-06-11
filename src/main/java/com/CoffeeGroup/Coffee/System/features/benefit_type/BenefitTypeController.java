package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/benefittypes")
public class BenefitTypeController {
    private final  BenefitTypeService benefitTypeService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    ResponseEntity<BenefitTypeResponse> create(@Valid @RequestBody BenefitTypeCreate request) {
        return new ResponseEntity<>(benefitTypeService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    ResponseEntity<List<BenefitTypeResponse>> AllTypeBenefit(){
        return ResponseEntity.ok(benefitTypeService.getAll());
    }
}
