package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreate;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/benefits")
public class BenefitController {

    private final BenefitService service;

    @PostMapping
    public ResponseEntity<BenefitResponse> create(@Valid @RequestBody BenefitCreate benefitCreate) {
        BenefitResponse response = service.save(benefitCreate);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BenefitResponse>> getAll() {
        return ResponseEntity.ok(service.all());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BenefitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<BenefitResponse> getByExternalId(@PathVariable UUID externalId) {
        return ResponseEntity.ok(service.findByExternalId(externalId));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<BenefitResponse> update(
            @PathVariable UUID externalId,
            @Valid @RequestBody BenefitCreate benefitCreate) {
        return ResponseEntity.ok(service.update(externalId, benefitCreate));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        service.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/points-cost/{pointCost}")
    public ResponseEntity<List<BenefitResponse>> shareByPointCost(@PathVariable Integer pointCost) {
        return ResponseEntity.ok(service.shareByPointCost(pointCost));
    }

    @GetMapping("/filter/end-date/{date}")
    public ResponseEntity<List<BenefitResponse>> shareByEndDate(@PathVariable Date date) {
        return ResponseEntity.ok(service.shareByEndDate(date));
    }

    @GetMapping("/filter/status/{status}")
    public ResponseEntity<List<BenefitResponse>> shareByStatus(@PathVariable Boolean status) {
        return ResponseEntity.ok(service.shareByStatus(status));
    }
}