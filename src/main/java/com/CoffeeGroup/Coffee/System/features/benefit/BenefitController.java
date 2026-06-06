package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
public class BenefitController {

    private final IBenefitService benefitService;

    @PostMapping
    ResponseEntity<BenefitResponse> create(@Valid @RequestBody BenefitCreateRequest CreateRequest){
        return new ResponseEntity<>(benefitService.save(CreateRequest),HttpStatus.CREATED);
    }

    @DeleteMapping("/{externalId}")
    ResponseEntity<BenefitResponse> delete(@PathVariable UUID externalId){
        benefitService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<List<BenefitResponse>> listAll(){
        return ResponseEntity.ok(benefitService.findAll());
    }

    @GetMapping("/{externalId}")
    ResponseEntity<BenefitResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(benefitService.findByBenefit(externalId));
    }

    @PostMapping("/{externalId}/publish")
    ResponseEntity<BenefitResponse> publishBenefit(UUID externalId){
        return ResponseEntity.ok(benefitService.confirmBenefit(externalId));
    }
    @PostMapping("/{externalId}/cancel")
    ResponseEntity<BenefitResponse> cancelBenefit(UUID externalId){
        return ResponseEntity.ok(benefitService.cancelBenefit(externalId));
    }

    @PostMapping("/{externalId}/changeEndDate")
    ResponseEntity<BenefitResponse> changeEnd(@PathVariable UUID externalId, @Valid @RequestBody NewEndDateRequest newEndDate){
       return ResponseEntity.ok(benefitService.changeEndDate(externalId,newEndDate));
    }

}