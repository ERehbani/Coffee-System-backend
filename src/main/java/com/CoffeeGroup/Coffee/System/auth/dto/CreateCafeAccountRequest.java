package com.CoffeeGroup.Coffee.System.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCafeAccountRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "The password must be at least 6 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, and one number"
        )
        String password,

        @NotBlank(message = "The name of the cafe is required")
        @Size(min = 2, max = 80, message = "The name must be between 2 and 80 characters long")
        String name,

        @NotBlank(message = "The address is required")
        @Size(min = 5, max = 150, message = "The address must be between 5 and 150 characters long")
        String address,

        @Size(max = 500, message = "The description cannot exceed 500 characters")
        String description,

        @Pattern(
                regexp = "^[0-9+\\-\\s()]{6,20}$",
                message = "The phone number is not in a valid format"
        )
        String phoneNumber,

        @Size(max = 150, message = "The time cannot exceed 150 characters")
        String schedule
) {
}