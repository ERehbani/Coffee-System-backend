package com.CoffeeGroup.Coffee.System.features.redemption.domain.mapper;

import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class RedemptionMapper {

    public RedemptionEntity toEntity(RedemptionCreateRequest request) {
        if (request == null) {
            return null;
        }
        return RedemptionEntity.builder().build();
    }

    public RedemptionResponse toResponse(RedemptionEntity entity) {
        if (entity == null) {
            return null;
        }

        LocalDate date = entity.getDate() != null
                ? LocalDate.from(entity.getDate().toInstant(ZoneOffset.UTC))
                : null;

        return new RedemptionResponse(
                entity.getExternalId(),
                date,
                entity.getUsedPoints(),
                entity.getValidationCode(),
                entity.getState(),
                entity.getPurchase().getExternalId(),
                entity.getBenefit().getExternalId(),
                entity.getClient().getExternalId()
        );
    }
}
