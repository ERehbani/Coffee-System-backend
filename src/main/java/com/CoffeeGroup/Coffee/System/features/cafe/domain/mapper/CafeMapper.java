package com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.OpeningHours;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursResponse;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository.CafeReviewSummary;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeMapper {

    private final Clock clock;
    private final ReviewRepository reviewRepository;

    public CafeResponse toResponse(CafeEntity cafe) {
        return toResponses(List.of(cafe)).getFirst();
    }

    public List<CafeResponse> toResponses(List<CafeEntity> cafes) {
        if (cafes.isEmpty()) {
            return List.of();
        }
        Map<Long, CafeReviewSummary> reviewsByCafe = reviewRepository.summarizeByCafe(cafes).stream()
                .collect(Collectors.toMap(CafeReviewSummary::getCafeId, Function.identity()));
        return cafes.stream()
                .map(cafe -> toResponse(cafe, reviewsByCafe.getOrDefault(cafe.getId(), CafeReviewSummary.NONE)))
                .toList();
    }

    public CafeResponse toResponse(CafeEntity cafe, CafeReviewSummary reviews) {
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
                cafe.hasCompleteProfile(),
                reviews.rating(),
                reviews.reviewCount());
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
