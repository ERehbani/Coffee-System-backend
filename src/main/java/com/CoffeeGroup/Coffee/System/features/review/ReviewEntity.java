package com.CoffeeGroup.Coffee.System.features.review;

import java.time.LocalDate;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ReviewEntity extends AuditableEntity {

    @Column(name = "message")
    private String message;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private CafeEntity cafe;

    @OneToOne
    @JoinColumn(name = "purchase_id", unique = true)
    private PurchaseEntity purchase;

    public boolean isWrittenBy(ClientEntity author) {
        return client != null && client.isSameAs(author);
    }
}
