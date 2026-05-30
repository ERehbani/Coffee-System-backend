package com.CoffeeGroup.Coffee.System.features.shared_rewards;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "shared_reward")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SharedRewardsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(nullable = false)
    private java.time.LocalDate date;

    //RELACION CON BENEFICIO
    @OneToOne
    @JoinColumn(name = "benefit_id")
    private BenefitEntity benefit;

    @ManyToOne
    @JoinColumn(name = "transmitter_id", nullable = false)
    private ClientEntity transmitter;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private ClientEntity receiver;

}
