package com.CoffeeGroup.Coffee.System.features.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CreateCartRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartRequest;
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

    @Override
    public List<CartResponse> getAll() {
        return cartRepository.findAll()
                .stream()
                .map(cartMapper::toDto)
                .toList();
    }

    @Override
    public CartResponse getById(UUID externalId) {
        return cartRepository.findByExternalId(externalId)
                .map(cartMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Override
    @Transactional
    public CartResponse create(CreateCartRequest request) {
        List<CartItem> items = request.items() != null
                ? request.items().stream()
                        .map(this::toCartItem)
                        .collect(Collectors.toList())
                : List.of();

        BigDecimal totalPrice = calculateTotalPrice(items);

        CartEntity entity = CartEntity.builder()
                .externalId(UUID.randomUUID())
                .clientId(request.clientId())
                .items(items)
                .totalPrice(totalPrice)
                .build();

        return cartMapper.toDto(cartRepository.save(entity));
    }

    @Override
    @Transactional
    public CartResponse update(UpdateCartRequest request) {
        var client = securityUtils.getCurrentClient();
        if (client == null) {
            throw new EntityNotFoundException("Client not found");
        }

        CartEntity cart = client.getCart();
        if (cart == null) {
            throw new EntityNotFoundException("Cart not found");
        }

        if (request.items() != null) {
            List<CartItem> existingItems = cart.getItems() != null ? cart.getItems() : List.of();
            List<CartItem> newItems = request.items().stream()
                    .map(this::toCartItem)
                    .collect(Collectors.toList());

            List<CartItem> mergedItems = mergeItems(existingItems, newItems);
            cart.setItems(mergedItems);
            cart.setTotalPrice(calculateTotalPrice(mergedItems));
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    private List<CartItem> mergeItems(List<CartItem> existingItems, List<CartItem> newItems) {
        Map<UUID, CartItem> itemMap = new HashMap<>();

        for (CartItem existing : existingItems) {
            itemMap.put(existing.getProductId(), existing);
        }

        for (CartItem newItem : newItems) {
            UUID productId = newItem.getProductId();
            if (itemMap.containsKey(productId)) {
                CartItem existing = itemMap.get(productId);
                CartItem merged = CartItem.builder()
                        .productId(productId)
                        .quantity(existing.getQuantity() + newItem.getQuantity())
                        .build();
                itemMap.put(productId, merged);
            } else {
                itemMap.put(productId, newItem);
            }
        }

        return new ArrayList<>(itemMap.values());
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        CartEntity cart = cartRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        cartRepository.delete(cart);
    }

    private CartItem toCartItem(CartItemRequest request) {
        return CartItem.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();
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
