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
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    @GetMapping
    ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("{productId}")
    ResponseEntity<ProductResponse> getProductById(UUID productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping
    ResponseEntity<ProductResponse> save(@RequestBody ProductRequest productBody){
        return new ResponseEntity<>(productService.save(productBody), HttpStatus.CREATED);
    }

    @PutMapping("{productId}")
    ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productBody, UUID productId){
        return ResponseEntity.ok(productService.update(productBody, productId));
    }

}
