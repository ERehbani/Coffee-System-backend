package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CreateCartRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartRequest;

public interface ICartService {

    List<CartResponse> getAll();

    CartResponse getById(UUID externalId);

    CartResponse create(CreateCartRequest request);

    CartResponse update(UUID externalId, UpdateCartRequest request);

    void delete(UUID externalId);

}
