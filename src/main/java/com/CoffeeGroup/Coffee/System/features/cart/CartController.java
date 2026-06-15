package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final ICartService cartService;

    @GetMapping("/myCart")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    ResponseEntity<List<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/{cafeExternalId}/items")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    ResponseEntity<CartResponse> addItem(
            @PathVariable UUID cafeExternalId,
            @Valid @RequestBody CartItemRequest request) {
        return new ResponseEntity<>(cartService.addItem(cafeExternalId, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{productExternalId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    ResponseEntity<CartResponse> removeItem(@PathVariable UUID productExternalId) {
        return ResponseEntity.ok(cartService.removeItem(productExternalId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

}
