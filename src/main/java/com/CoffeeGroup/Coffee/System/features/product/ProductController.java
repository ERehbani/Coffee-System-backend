package com.CoffeeGroup.Coffee.System.features.product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

   // @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{productId}")
    ResponseEntity<ProductResponse> getById(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID cafe){
        return ResponseEntity.ok(productService.getById(productId,cafe));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA','ROLE_SUPERADMIN')")
    @PostMapping
    ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest productBody){
        return new ResponseEntity<>(productService.create(productBody), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA')")
    @PutMapping("/{productId}")
    ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productBody, @PathVariable UUID productId){
        return ResponseEntity.ok(productService.update(productBody, productId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId){
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }


}
