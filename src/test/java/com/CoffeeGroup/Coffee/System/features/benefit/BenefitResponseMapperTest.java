package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitResponseMapperImpl;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;

class BenefitResponseMapperTest {

    private final IBenefitResponseMapperImpl mapper = new IBenefitResponseMapperImpl();

    @Test
    void theResponseCarriesTheStartDateAndTheTypeAndTierIdsTheEditFormSendsBack() {
        UUID typeId = UUID.randomUUID();
        UUID tierId = UUID.randomUUID();
        BenefitEntity benefit = BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Cafe gratis")
                .pointsCost(100)
                .startDate(LocalDate.of(2026, 11, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .benefitType(BenefitTypeEntity.builder().externalId(typeId).name(BenefitType.DOS_POR_UNO).build())
                .tierEntity(TierEntity.builder().externalId(tierId).name("Oro").minPoints(500).build())
                .build();

        BenefitResponse response = mapper.toDto(benefit);

        assertThat(response.startDate()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(response.endDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.typeId()).isEqualTo(typeId);
        assertThat(response.tierId()).isEqualTo(tierId);
        assertThat(response.benefitType()).isEqualTo(BenefitType.DOS_POR_UNO);
        assertThat(response.tierName()).isEqualTo("Oro");
    }

    @Test
    void aBenefitWithoutTypeTierOrDatesMapsThemToNull() {
        BenefitEntity benefit = BenefitEntity.builder().externalId(UUID.randomUUID()).name("Legacy").pointsCost(50).build();

        BenefitResponse response = mapper.toDto(benefit);

        assertThat(response.startDate()).isNull();
        assertThat(response.typeId()).isNull();
        assertThat(response.tierId()).isNull();
        assertThat(response.tierName()).isNull();
    }
}
