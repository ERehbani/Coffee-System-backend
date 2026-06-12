package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CreateCartRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final ICartService cartService;

    @GetMapping
    ResponseEntity<List<CartResponse>> getAll() {
        return ResponseEntity.ok(cartService.getAll());
    }

    @GetMapping("/{externalId}")
    ResponseEntity<CartResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(cartService.getById(externalId));
    }

    @PostMapping
    ResponseEntity<CartResponse> create(@Valid @RequestBody CreateCartRequest request) {
        return new ResponseEntity<>(cartService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{externalId}")
    ResponseEntity<CartResponse> update(@PathVariable UUID externalId, @Valid @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(cartService.update(externalId, request));
    }

    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        cartService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

}
