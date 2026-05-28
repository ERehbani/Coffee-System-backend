package com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreate;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BenefitMapper {

    public BenefitEntity toEntity(BenefitCreate dto, BenefitTypeEntity type, CafeEntity cafe, TierEntity tier) {
        return BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .description(dto.description())
                .price(dto.price())
                .pointsCost(dto.pointsCost())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .status(true)
                .isPublic(dto.isPublic())
                .maxRedemptions(dto.maxRedemptions())
                .Type(type)
                .cafe(cafe)
                .tier(tier)
                .build();
    }

    public BenefitResponse toDto(BenefitEntity entity) {
        return new BenefitResponse(
                entity.getId(),
                entity.getExternalId(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getPointsCost(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getIsPublic(),
                entity.getMaxRedemptions(),
                
                entity.getType() != null ? entity.getType().getId() : null,
                entity.getType() != null ? entity.getType().getName() : null,
                entity.getCafe() != null ? entity.getCafe().getId() : null,
                entity.getCafe() != null ? entity.getCafe().getName() : null,
                entity.getTier() != null ? entity.getTier().getId() : null,
                entity.getTier() != null ? entity.getTier().getName() : null
        );
    }
}