package com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.OpeningHours;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeMapper {

    private final Clock clock;

    public CafeResponse toResponse(CafeEntity cafe) {
        return new CafeResponse(
                cafe.getExternalId(),
                cafe.getName(),
                cafe.getAddress(),
                cafe.email(),
                cafe.getPhoneNumber(),
                cafe.getDescription(),
                cafe.isActive(),
                cafe.getLogo(),
                cafe.getImage(),
                cafe.getOpeningHours().stream().map(CafeMapper::toResponse).toList(),
                cafe.isOpenAt(LocalDateTime.now(clock)),
                cafe.hasCompleteProfile());
    }

    public List<OpeningHours> toOpeningHours(List<OpeningHoursRequest> requests) {
        return requests.stream()
                .map(request -> OpeningHours.of(request.day(), request.opensAt(), request.closesAt()))
                .toList();
    }

    private static OpeningHoursResponse toResponse(OpeningHours hours) {
        return new OpeningHoursResponse(hours.getDay(), hours.getOpensAt(), hours.getClosesAt());
    }
}
