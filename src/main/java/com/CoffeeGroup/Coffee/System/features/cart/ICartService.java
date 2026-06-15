package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;

public interface ICartService {
    List<CartResponse> getMyCart();
    CartResponse addItem(UUID cafeExternalId, CartItemRequest request);
    CartResponse removeItem(UUID productExternalId);
    void clearCart();

}
