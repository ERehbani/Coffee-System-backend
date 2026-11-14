package com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;

@Component
public class CafeMapper {

    public CafeResponse toResponse(CafeEntity cafe) {
        return new CafeResponse(
                cafe.getExternalId(),
                cafe.getName(),
                cafe.getAddress(),
                cafe.email(),
                cafe.getPhoneNumber(),
                cafe.getDescription(),
                cafe.isActive(),
                cafe.getSchedule(),
                cafe.getImage());
    }
}
