package com.CoffeeGroup.Coffee.System.features.purchase;


import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionUpdateRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService service;

    @PostMapping
    public ResponseEntity<PurchaseResponse> save(@RequestBody CreatePurchaseRequest create){

        return new ResponseEntity<>(service.create(create), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable UUID externalId){
        return ResponseEntity.ok(service.getById(externalId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponse> update(@PathVariable UUID externalId, @RequestBody UpdatePurchaseRequest update){
        return ResponseEntity.ok(service.update(externalId,update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PurchaseResponse> delete (UUID externalId){
        service.delete(externalId);

        return ResponseEntity.ok().build();
    }




}
