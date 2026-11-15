package com.CoffeeGroup.Coffee.System.features.media.domain.dto;

public record UploadSignatureResponse(
        String cloudName,
        String apiKey,
        long timestamp,
        String folder,
        String allowedFormats,
        String signature) {
}
