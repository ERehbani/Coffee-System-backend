package com.CoffeeGroup.Coffee.System.features.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.exceptions.CartHasNoProductsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CartEntity extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private CafeEntity cafe;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items = new ArrayList<>();

    public static CartEntity openFor(ClientEntity client, CafeEntity cafe) {
        return CartEntity.builder().client(client).cafe(cafe).build();
    }

    public void add(CafeProductEntity product, int quantity) {
        product.ensureCanBeSoldBy(cafe);
        findItem(product.getExternalId()).ifPresentOrElse(
                item -> item.setQuantity(item.getQuantity() + quantity),
                () -> items.add(CartItemEntity.builder().cart(this).product(product).quantity(quantity).build()));
    }

    public void changeQuantity(UUID productExternalId, int quantity) {
        requireItem(productExternalId).setQuantity(quantity);
    }

    public void remove(UUID productExternalId) {
        items.remove(requireItem(productExternalId));
    }

    public boolean contains(UUID productExternalId) {
        return findItem(productExternalId).isPresent();
    }

    public void clear() {
        items.clear();
    }

    public void ensureNotEmpty() {
        if (items.isEmpty()) {
            throw new CartHasNoProductsException("Cart cannot be empty");
        }
    }

    public BigDecimal total() {
        return items.stream().map(CartItemEntity::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean belongsTo(ClientEntity otherClient) {
        return client != null && client.isSameAs(otherClient);
    }

    private Optional<CartItemEntity> findItem(UUID productExternalId) {
        return items.stream().filter(item -> item.getProduct().getExternalId().equals(productExternalId)).findFirst();
    }

    private CartItemEntity requireItem(UUID productExternalId) {
        return findItem(productExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in cart"));
    }
}
