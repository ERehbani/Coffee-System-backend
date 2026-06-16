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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
@Tag(name = "Cart", description = "Endpoints for managing shopping cart")
public class CartController {

    private final ICartService cartService;

    @GetMapping("/myCart")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Get my cart", description = "Retrieves the current user's shopping cart")
    @ApiResponses
    ResponseEntity<List<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/{cafeExternalId}/items")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Add item to cart", description = "Adds a product to the user's shopping cart")
    @ApiResponses
    ResponseEntity<CartResponse> addItem(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeExternalId,
            @Valid @RequestBody CartItemRequest request) {
        return new ResponseEntity<>(cartService.addItem(cafeExternalId, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{productExternalId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Remove item from cart", description = "Removes a product from the user's shopping cart")
    @ApiResponses
    ResponseEntity<CartResponse> removeItem(@Parameter(description = "Product external ID") @PathVariable UUID productExternalId) {
        return ResponseEntity.ok(cartService.removeItem(productExternalId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Clear cart", description = "Clears all items from the user's shopping cart")
    @ApiResponses
    ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

}
