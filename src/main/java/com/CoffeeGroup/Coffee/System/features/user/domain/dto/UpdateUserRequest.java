package com.CoffeeGroup.Coffee.System.features.user.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email(message = "Invalid Email")
        String email,
        Boolean state
        ) {
}
