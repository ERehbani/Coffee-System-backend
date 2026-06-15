package com.CoffeeGroup.Coffee.System.features.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.mapper.CartMapper;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CartService implements ICartService {

    private final ICartRepository cartRepository;
    private final CartMapper cartMapper;
    private final SecurityUtils securityUtils;
    private final IProductRepository productRepository;
    private final CafeRepository cafeRepository;

    @Override
    public List<CartResponse> getMyCart() {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) throw new EntityNotFoundException("Client not found");

        return cartRepository.findByClient(client).stream()
                .map(cartMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID cafeExternalId, CartItemRequest request) {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) throw new EntityNotFoundException("Client not found");

        CafeEntity cafe = cafeRepository.findByExternalId(cafeExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

        ProductEntity product = productRepository
                .findByExternalIdAndCafeExternalId(request.productId(), cafeExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in this cafe"));

        CartEntity cart = cartRepository.findByClientAndCafeEntity(client, cafe)
                .orElseGet(() -> CartEntity.builder()
                        .externalId(UUID.randomUUID())
                        .client(client)
                        .cafeEntity(cafe)
                        .items(new ArrayList<>())
                        .totalPrice(BigDecimal.ZERO)
                        .build());

        List<CartItem> items = cart.getItems();
        boolean exists = false;
        for (CartItem item : items) {
            if (item.getProductId().equals(request.productId())) {
                item.setQuantity(item.getQuantity() + request.quantity());
                exists = true;
                break;
            }
        }
        if (!exists) {
            items.add(CartItem.builder()
                    .productId(request.productId())
                    .quantity(request.quantity())
                    .build());
        }

        cart.setTotalPrice(calculateTotalPrice(items));
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID productExternalId) {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) throw new EntityNotFoundException("Client not found");

        List<CartEntity> carts = cartRepository.findByClient(client);
        CartEntity cart = carts.stream()
                .filter(c -> c.getItems().stream().anyMatch(item -> item.getProductId().equals(productExternalId)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product not found in any cart"));

        boolean removed = cart.getItems()
                .removeIf(item -> item.getProductId().equals(productExternalId));

        if (!removed) throw new EntityNotFoundException("Product not found in cart");

        cart.setTotalPrice(calculateTotalPrice(cart.getItems()));
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart() {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) throw new EntityNotFoundException("Client not found");

        List<CartEntity> carts = cartRepository.findByClient(client);
        carts.forEach(cart -> {
            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
        });
        cartRepository.saveAll(carts);
    }

    private BigDecimal calculateTotalPrice(List<CartItem> items) {
        return items.stream()
                .map(item -> {
                    ProductEntity product = productRepository.findByExternalId(item.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                    return product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
