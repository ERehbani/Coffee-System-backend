package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @Size(min = 2, max = 50, message = "The name must be between 2 and 50 characters long")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "The name can only contain letters and spaces.")
        @Pattern(regexp = ".*\\S.*", message = "The name must not be blank")
        String name,

        @Size(min = 5, max = 100, message = "The description must be between 5 and 100 characters long")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "The description can only contain letters and numbers.")
        @Pattern(regexp = ".*\\S.*", message = "The description must not be blank")
        String description,

        @Min(value = 500, message = "The price must be bigger than 500")
        @Max(value = 50000, message = "The price must be smaller than 50000")
        BigDecimal price,

        @Min(value = 1, message = "Awarded points must be at least 1")
        @Max(value = 3000, message = "Awarded points must be smaller than 3000")
        Integer awardedPoints,

        @Size(max = 500, message = "The image must be at most 500 characters long")
        String image,

        @Size(max = 10, message = "A product can have at most 10 tags")
        List<@NotBlank(message = "Tags must not be blank")
             @Size(max = 30, message = "Each tag must be at most 30 characters long") String> tags
) {
}
