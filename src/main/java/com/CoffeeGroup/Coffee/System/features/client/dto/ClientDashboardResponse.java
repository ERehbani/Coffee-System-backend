package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Everything the client's profile home shows, in one payload. Field names mirror the frontend ProfileDashboardDto. */
public record ClientDashboardResponse(
        Profile client,
        Loyalty loyalty,
        List<ActiveRedemption> activeRedemptions,
        List<NearbyCafe> nearbyCafes,
        List<FeaturedBenefit> featuredBenefits
) {

    public record Profile(
            UUID id,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String avatarUrl
    ) {
    }

    public record Loyalty(
            String tier,
            Integer availablePoints,
            Integer lifetimePoints,
            String nextTier,
            Integer nextTierThreshold,
            Integer progressPercent,
            Integer pointsToNextTier
    ) {
    }

    public record ActiveRedemption(
            UUID id,
            String title,
            String description,
            String code,
            LocalDate expiresAt,
            String cafeName
    ) {
    }

    public record NearbyCafe(
            UUID id,
            String name,
            String imageUrl,
            Double rating,
            Integer reviewCount,
            boolean isOpenNow,
            Integer distanceMeters,
            Integer maxPointsPerProduct,
            String tagline
    ) {
    }

    public record FeaturedBenefit(
            UUID id,
            String title,
            String description,
            String imageUrl,
            Integer pointsCost
    ) {
    }
}
