package com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

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


    public void updateEntityFromRequest(UpdateCafeRequest request, CafeEntity existingEntity) {
        if (request == null || existingEntity == null) return;

        existingEntity.setAddress(request.address());
        existingEntity.setEmail(new Email(request.email()));
        existingEntity.setPhoneNumber(request.phoneNumber());
        existingEntity.setDescription(request.description());
        existingEntity.setSchedule(request.schedule());
        existingEntity.setImage(request.image());
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
