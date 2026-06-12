package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCafeRequest(
        @NotBlank(message = "Address cannot be empty")
        String address,

        @Pattern(regexp = "^\\d{10}$", message = "Phone number must have the correct length")
        String phoneNumber,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        String schedule,

        String image

) {
}
