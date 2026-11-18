package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.mapper.CartMapper;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final SecurityUtils securityUtils;
    private final CafeProductRepository cafeProductRepository;
    private final CafeRepository cafeRepository;

    @Override
    public List<CartResponse> getMyCart() {
        return cartRepository.findByClient(securityUtils.currentClient()).stream()
                .map(cartMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID cafeExternalId, CartItemRequest request) {
        ClientEntity client = securityUtils.currentClient();
        CafeEntity cafe = cafeRepository.findByExternalId(cafeExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));
        CafeProductEntity product = cafeProductRepository.findByExternalIdAndCafe(request.productId(), cafe)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado en esta cafetería"));

        CartEntity cart = cartRepository.findByClientAndCafe(client, cafe)
                .orElseGet(() -> CartEntity.openFor(client, cafe));
        cart.add(product, request.quantity());
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(UUID productExternalId, UpdateCartItemRequest request) {
        CartEntity cart = cartContaining(productExternalId);
        cart.changeQuantity(productExternalId, request.quantity());
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID productExternalId) {
        CartEntity cart = cartContaining(productExternalId);
        cart.remove(productExternalId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        cartRepository.findByClient(securityUtils.currentClient()).forEach(CartEntity::clear);
    }

    private CartEntity cartContaining(UUID productExternalId) {
        return cartRepository.findByClient(securityUtils.currentClient()).stream()
                .filter(cart -> cart.contains(productExternalId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado en ningún carrito"));
    }
}
