package com.CoffeeGroup.Coffee.System.features.cart.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartItemEntity;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;

@Component
public class CartMapper {

    public CartResponse toResponse(CartEntity cart) {
        return new CartResponse(
                cart.getExternalId(),
                cart.getClient().getExternalId(),
                cart.getCafe().getExternalId(),
                cart.total(),
                cart.getItems().stream().map(CartMapper::toItemResponse).toList());
    }

    private static CartItemResponse toItemResponse(CartItemEntity item) {
        return new CartItemResponse(
                item.getProduct().getExternalId(),
                item.getProduct().name(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.subtotal(),
                item.getProduct().canBeSold());
    }
}
