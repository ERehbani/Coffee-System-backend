package com.CoffeeGroup.Coffee.System.features.cafe;

import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/cafes")
public class CafeController {

 private final CafeService service;

 @PostMapping
 ResponseEntity<CafeResponse> create(@Valid @RequestBody CreateCafeRequest request){
     return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
 }

 @PutMapping("/{externalId}")
 ResponseEntity<CafeResponse> update(@PathVariable UUID externalId, @Valid @RequestBody UpdateCafeRequest request){
      return ResponseEntity.ok(service.update(externalId,request));
    }


 @DeleteMapping("/{externalId}")
  ResponseEntity<Void> delete(@PathVariable UUID externalId){
  service.delete(externalId);
   return ResponseEntity.noContent().build();
 }



    @GetMapping
    ResponseEntity<List<CafeResponse>> getAll(){return ResponseEntity.ok(service.getAll());}

    /*
    @GetMapping("/{externalId}")
    ResponseEntity<CafeResponse> getByExternalId(@PathVariable UUID externalId){
        return ResponseEntity.ok(service.getByExternalId(externalId));
    }

     */




}
