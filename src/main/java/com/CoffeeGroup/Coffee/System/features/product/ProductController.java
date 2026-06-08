package com.CoffeeGroup.Coffee.System.features.product;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/{productId}")
    ResponseEntity<ProductResponse> getById(@PathVariable UUID productId){
        return ResponseEntity.ok(productService.getById(productId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA')")
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
