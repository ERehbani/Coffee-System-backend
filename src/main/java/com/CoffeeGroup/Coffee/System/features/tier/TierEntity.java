package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tiers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "name")
    private String name;

    @Column(name = "min_points")
    private Integer minPoints;

    @OneToMany(mappedBy = "tier")
    private List<ClientEntity> client;



}
