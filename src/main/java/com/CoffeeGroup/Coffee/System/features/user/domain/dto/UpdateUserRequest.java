package com.CoffeeGroup.Coffee.System.features.user.domain.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        @Email(message = "Invalid Email")
        String email,
        Boolean state
        ) {
}
