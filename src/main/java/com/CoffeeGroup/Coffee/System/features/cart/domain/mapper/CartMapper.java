package com.CoffeeGroup.Coffee.System.features.cart.domain.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartItem;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CartMapper implements IMapper<CartEntity, CartResponse> {

    private final IProductRepository productRepository;

    @Override
    public CartEntity toEntity(CartResponse cartResponse) {
        /*if (cartResponse == null) return null;

        List<CartItem> items = cartResponse.products() != null
                ? cartResponse.products().stream()
                        .map(this::toCartItem)
                        .collect(Collectors.toList())
                : List.of();

        return CartEntity.builder()
                .externalId(cartResponse.externalId() != null ? cartResponse.externalId() : UUID.randomUUID())
                .client(cartResponse.clientid())
                .cafeEntity(cartResponse.cafeid())
                .totalPrice(cartResponse.totalPrice())
                .items(items)
                .build();

         */
        return null;
    }
    private CartItem toCartItem(CartItemResponse response) {
        return CartItem.builder()
                .productId(response.productId())
                .quantity(response.quantity())
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        String productName = productRepository.findByExternalId(item.getProductId())
                .map(ProductEntity::getName)
                .orElse("Unknown Product");

        return new CartItemResponse(
                item.getProductId(),
                productName,
                item.getQuantity()
        );
    }

    @Override
    public CartResponse toDto(CartEntity entity) {
        if (entity == null) return null;

        List<CartItemResponse> products = entity.getItems() != null
                ? entity.getItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList())
                : List.of();

        return new CartResponse(
                entity.getExternalId(),
                entity.getClient() != null ? entity.getClient().getExternalId() : null,
                entity.getCafeEntity() != null ? entity.getCafeEntity().getExternalId() : null,
                entity.getTotalPrice(),
                products
        );
    }

}
