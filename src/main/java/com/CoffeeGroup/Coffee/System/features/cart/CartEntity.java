package com.CoffeeGroup.Coffee.System.features.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "carts")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private CafeEntity cafeEntity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    private List<CartItem> items;

}
