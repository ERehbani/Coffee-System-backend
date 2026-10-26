package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A benefit format admitted on the platform. The closed {@link BenefitType} enum stays the source of the discount logic. */
@Entity
@Table(name = "benefit_formats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitFormatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true)
    private BenefitType code;

    @Column(name = "display_name", nullable = false, length = 40)
    private String displayName;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
