package com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;

import org.springframework.stereotype.Component;

@Component
public class CafeMapper {

    public CafeEntity toEntity(CreateCafeRequest request) {
        if (request == null) return null;

        return CafeEntity.builder()
                .externalId(UUID.randomUUID())
                .name(request.name())
                .address(request.address())
                .email(new Email(request.email()))
                .phoneNumber(request.phoneNumber())
                .description(request.description())
                .schedule(request.schedule())
                .image(request.image())
                .state(true)
                .build();
    }

    public CafeResponse toResponse(CafeEntity entity) {
        if (entity == null) return null;

        return new CafeResponse(
                entity.getExternalId(),
                entity.getName(),
                entity.getAddress(),
                entity.getEmail() != null ? entity.getEmail().value() : null,
                entity.getPhoneNumber(),
                entity.getDescription(),
                entity.getState(),
                entity.getSchedule(),
                entity.getImage()
        );
    }

}
