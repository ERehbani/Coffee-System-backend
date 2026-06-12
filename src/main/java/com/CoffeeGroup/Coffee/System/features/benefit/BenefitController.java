package com.CoffeeGroup.Coffee.System.features.benefit;

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

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final IBenefitService benefitService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping
    ResponseEntity<BenefitResponse> create(@Valid @RequestBody BenefitCreateRequest CreateRequest){
        return new ResponseEntity<>(benefitService.save(CreateRequest),HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @DeleteMapping("/{externalId}")
    ResponseEntity<BenefitResponse> delete(@PathVariable UUID externalId){
        benefitService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA','ROLE_CLIENTE')")
    @GetMapping
    ResponseEntity<List<BenefitResponse>> listAll(){
        return ResponseEntity.ok(benefitService.getAll());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{externalId}")
    ResponseEntity<BenefitResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(benefitService.getByBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/publish")
    ResponseEntity<BenefitResponse> publishBenefit(@PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.confirmBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/cancel")
    ResponseEntity<BenefitResponse> cancelBenefit(@PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.cancelBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/changeEndDate")
    ResponseEntity<BenefitResponse> changeEnd(@PathVariable UUID externalId, @Valid @RequestBody NewEndDateRequest newEndDate){
       return ResponseEntity.ok(benefitService.changeEndDate(externalId,newEndDate));
    }

}