package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCafeRequest(
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String name,

        @NotBlank(message = "Address cannot be empty")
        String address,

        @Pattern(regexp = "^\\d{10}$", message = "Phone number must have the correct length")
        String phoneNumber,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Size(max = 500, message = "The logo cannot exceed 500 characters")
        String logo,

        @Size(max = 500, message = "The image cannot exceed 500 characters")
        String image,

        List<@Valid OpeningHoursRequest> openingHours
) {
}
