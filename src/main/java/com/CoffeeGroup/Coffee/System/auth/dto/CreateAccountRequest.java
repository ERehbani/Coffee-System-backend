package com.CoffeeGroup.Coffee.System.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "The password must be at least 6 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "The password must contain at least one uppercase letter, one lowercase letter, and one number"
        )
        String password
) {
}
