package com.CoffeeGroup.Coffee.System.features.product;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping
    ResponseEntity<ProductResponse> getById(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID cafe){
        return ResponseEntity.ok(productService.getById(productId,cafe));
    }

    @PostMapping
    ResponseEntity<ProductResponse> create(@RequestBody ProductRequest productBody){
        return new ResponseEntity<>(productService.create(productBody), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productBody, @PathVariable UUID productId){
        return ResponseEntity.ok(productService.update(productBody, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId){
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{externalId}/Cafe")
    public ResponseEntity<List<ProductResponse>> findCafeProduts(@PathVariable UUID externalId){
        return ResponseEntity.ok(productService.ListProducts(externalId));
    }


}
