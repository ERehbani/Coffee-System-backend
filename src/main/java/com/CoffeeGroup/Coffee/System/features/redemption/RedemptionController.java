package com.CoffeeGroup.Coffee.System.features.redemption;

import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/redemption")
public class RedemptionController {

    private final IRedemptionService redemptionService;

    @PostMapping
    ResponseEntity<RedemptionResponse> create(@Valid @RequestBody RedemptionCreateRequest redemptionCreateRequest){
        return new ResponseEntity<>(redemptionService.create(redemptionCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RedemptionResponse>> getAll() {
        return ResponseEntity.ok(redemptionService.findAll());
    }

    @PutMapping("/{externalId}")
    ResponseEntity<RedemptionResponse> update(@PathVariable UUID externalId, @RequestBody RedemptionUpdateRequest redemptionUpdateRequest) {
        return ResponseEntity.ok(redemptionService.update(externalId, redemptionUpdateRequest));
    }

    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        redemptionService.deleteByid(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{externalId}")
    ResponseEntity<RedemptionResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(redemptionService.getById(externalId));
    }


}
