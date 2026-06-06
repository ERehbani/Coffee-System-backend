package com.CoffeeGroup.Coffee.System.features.cart;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}
