package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;

public interface ICartService {
    CartResponse getMyCart(UUID cafeExternalId);
    CartResponse addItem(UUID cafeExternalId, CartItemRequest request);
    CartResponse removeItem(UUID cafeExternalId, UUID productExternalId);
    void clearCart(UUID cafeExternalId);

}
