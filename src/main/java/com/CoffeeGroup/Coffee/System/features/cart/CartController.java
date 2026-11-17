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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartItemRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
@Tag(name = "Carrito", description = "Endpoints para administrar el carrito de compras")
public class CartController {

    private final ICartService cartService;

    @GetMapping("/myCart")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Obtener mi carrito", description = "Obtiene el carrito del usuario actual")
    @ApiResponses
    ResponseEntity<List<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/{cafeExternalId}/items")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito del usuario")
    @ApiResponses
    ResponseEntity<CartResponse> addItem(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeExternalId,
            @Valid @RequestBody CartItemRequest request) {
        return new ResponseEntity<>(cartService.addItem(cafeExternalId, request), HttpStatus.CREATED);
    }

    @PutMapping("/items/{productExternalId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Actualizar cantidad del producto", description = "Actualiza la cantidad de un producto en el carrito del usuario")
    @ApiResponses
    ResponseEntity<CartResponse> updateItemQuantity(
            @Parameter(description = "ID externo del producto") @PathVariable UUID productExternalId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(productExternalId, request));
    }

    @DeleteMapping("/items/{productExternalId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Quitar producto del carrito", description = "Quita un producto del carrito del usuario")
    @ApiResponses
    ResponseEntity<CartResponse> removeItem(@Parameter(description = "ID externo del producto") @PathVariable UUID productExternalId) {
        return ResponseEntity.ok(cartService.removeItem(productExternalId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Vaciar carrito", description = "Vacía el carrito del usuario")
    @ApiResponses
    ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

}
