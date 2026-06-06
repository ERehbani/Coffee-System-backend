package com.CoffeeGroup.Coffee.System.features.cart;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CreateCartRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.mapper.CartMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CartService implements ICartService {

    private final ICartRepository cartRepository;
    private final CartMapper cartMapper;

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
    public CartResponse update(UUID externalId, UpdateCartRequest request) {
        CartEntity cart = cartRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (request.items() != null) {
            List<CartItem> items = request.items().stream()
                    .map(this::toCartItem)
                    .collect(Collectors.toList());
            cart.setItems(items);
            cart.setTotalPrice(calculateTotalPrice(items));
        }

        if (request.totalPrice() != null) {
            cart.setTotalPrice(request.totalPrice());
        }

        return cartMapper.toDto(cartRepository.save(cart));
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
        return BigDecimal.ZERO;
    }

}
