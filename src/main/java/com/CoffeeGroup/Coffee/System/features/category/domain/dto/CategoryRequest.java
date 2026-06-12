package com.CoffeeGroup.Coffee.System.features.category.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name must be not blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        @Pattern(
                regexp = "^\\p{L}+$",
                message = "Name must only contain letters, no numbers, symbols or spaces"
        )
        String name
) {
}
