package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(benefitService.findById(externalId));
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

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/filter")
    ResponseEntity<List<BenefitResponse>> filterBenefits(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) BenefitType type) {
        return ResponseEntity.ok(benefitService.filterBenefitsByCafe(cafeId, name, status, isPublic, type));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}")
    ResponseEntity<List<BenefitResponse>> getBenefitsByCafe(@PathVariable UUID cafeId){
        return ResponseEntity.ok(benefitService.getAllByCafe(cafeId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/{benefitId}")
    ResponseEntity<BenefitResponse> getBenefitByCafe(@PathVariable UUID cafeId, @PathVariable UUID benefitId){
        return ResponseEntity.ok(benefitService.getBenefitByCafe(cafeId, benefitId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/{clientId}")
    ResponseEntity<List<BenefitResponse>> getAllByClient(@PathVariable UUID clientId){
        return ResponseEntity.ok(benefitService.getAllByClient(clientId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/{clientId}/filter")
    ResponseEntity<List<BenefitResponse>> filterBenefitsByClient(
            @PathVariable UUID cafeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) BenefitType type){
        return ResponseEntity.ok(benefitService.filterBenefitsByClient(cafeId, name, status, type));
    }



}