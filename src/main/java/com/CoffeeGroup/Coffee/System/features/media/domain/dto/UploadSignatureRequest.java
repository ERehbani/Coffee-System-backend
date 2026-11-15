package com.CoffeeGroup.Coffee.System.features.media.domain.dto;

import com.CoffeeGroup.Coffee.System.features.media.UploadTarget;

import jakarta.validation.constraints.NotNull;

public record UploadSignatureRequest(@NotNull UploadTarget target) {
}
