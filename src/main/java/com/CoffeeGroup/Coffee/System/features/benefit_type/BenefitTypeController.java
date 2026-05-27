package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/benefittypes")
public class BenefitTypeController {
    private final  BenefitTypeService benefitTypeService;

    @PostMapping
    ResponseEntity<BenefitTypeResponse> save(@Valid @RequestBody BenefitTypeCreate benefitTypeCreate){
        return new ResponseEntity<>(benefitTypeService.create(benefitTypeCreate), HttpStatus.CREATED);
    }

    @PutMapping("/{externalId}")
    ResponseEntity<BenefitTypeResponse> update(@PathVariable UUID externalId, @Valid @RequestBody BenefitTypeCreate benefitTypeCreate){
        return ResponseEntity.ok(benefitTypeService.update(externalId,benefitTypeCreate));
    }

    @DeleteMapping("/{externalId}")
    ResponseEntity<BenefitTypeResponse> delete(@PathVariable UUID externalId){
        benefitTypeService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<List<BenefitTypeResponse>> getAll(){
        return ResponseEntity.ok(benefitTypeService.getAll());
    }

}
