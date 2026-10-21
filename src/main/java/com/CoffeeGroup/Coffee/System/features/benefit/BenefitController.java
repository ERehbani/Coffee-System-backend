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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
@Tag(name = "Benefits", description = "Endpoints for managing benefits and rewards")
public class BenefitController {

    private final IBenefitService benefitService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping
    @Operation(summary = "Create benefit", description = "Creates a new benefit for a cafeteria")
    @ApiResponses
    ResponseEntity<BenefitResponse> create(@Valid @RequestBody BenefitCreateRequest CreateRequest){
        return new ResponseEntity<>(benefitService.save(CreateRequest),HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete benefit", description = "Deletes a benefit by its external ID")
    @ApiResponses
    ResponseEntity<BenefitResponse> delete(@Parameter(description = "Benefit external ID") @PathVariable UUID externalId){
        benefitService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA','ROLE_CLIENTE')")
    @GetMapping
    @Operation(summary = "List all benefits", description = "Retrieves all benefits in the system")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> listAll(){
        return ResponseEntity.ok(benefitService.getAll());
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Get benefit by ID", description = "Retrieves a specific benefit by its external ID")
    @ApiResponses
    ResponseEntity<BenefitResponse> getById(@Parameter(description = "Benefit external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(benefitService.findById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/publish")
    @Operation(summary = "Publish benefit", description = "Publishes a benefit making it available to clients")
    @ApiResponses
    ResponseEntity<BenefitResponse> publishBenefit(@Parameter(description = "Benefit external ID") @PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.confirmBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/cancel")
    @Operation(summary = "Cancel benefit", description = "Cancels a benefit making it unavailable")
    @ApiResponses
    ResponseEntity<BenefitResponse> cancelBenefit(@Parameter(description = "Benefit external ID") @PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.cancelBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/changeEndDate")
    @Operation(summary = "Change benefit end date", description = "Updates the end date of a benefit")
    @ApiResponses
    ResponseEntity<BenefitResponse> changeEnd(@Parameter(description = "Benefit external ID") @PathVariable UUID externalId, @Valid @RequestBody NewEndDateRequest newEndDate){
       return ResponseEntity.ok(benefitService.changeEndDate(externalId,newEndDate));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/filter")
    @Operation(summary = "Filter benefits by cafe", description = "Filters benefits for a specific cafeteria by various criteria")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterBenefits(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Filter by public visibility") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "Filter by benefit type") @RequestParam(required = false) BenefitType type) {
        return ResponseEntity.ok(benefitService.filterBenefitsByCafe(cafeId, name, status, isPublic, type));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}")
    @Operation(summary = "Get benefits by cafe", description = "Retrieves all benefits for a specific cafeteria")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> getBenefitsByCafe(@Parameter(description = "Cafe external ID") @PathVariable UUID cafeId){
        return ResponseEntity.ok(benefitService.getAllByCafe(cafeId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/{benefitId}")
    @Operation(summary = "Get specific benefit by cafe", description = "Retrieves a specific benefit for a cafeteria")
    @ApiResponses
    ResponseEntity<BenefitResponse> getBenefitByCafe(@Parameter(description = "Cafe external ID") @PathVariable UUID cafeId, @Parameter(description = "Benefit external ID") @PathVariable UUID benefitId){
        return ResponseEntity.ok(benefitService.getBenefitByCafe(cafeId, benefitId));
    }

    /*la diferencia de este y el de 2 endpoints mas arriba
     es que este lo usan los clientes y muestra los disponibles
     el otro lo utiliza solo la cafeteria para poder ver absolutamente todos*/

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/available/filter")
    @Operation(summary = "Filter available benefits", description = "Filters available benefits for clients by various criteria")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterAvailableBenefits(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by benefit type") @RequestParam(required = false) BenefitType type,
            @Parameter(description = "Filter by maximum points required") @RequestParam(required = false) Integer maxPoints) {
        return ResponseEntity.ok(benefitService.filterAvailableBenefits(cafeId, name, type, maxPoints));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/global/filter")
    @Operation(summary = "Filter global benefits", description = "Filters available global benefits across all cafeterias")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterAvailableGlobalBenefits(
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by benefit type") @RequestParam(required = false) BenefitType type,
            @Parameter(description = "Filter by maximum points required") @RequestParam(required = false) Integer maxPoints) {
        return ResponseEntity.ok(benefitService.filterAvailableGlobalBenefits(name, type, maxPoints));
    }
}