package com.CoffeeGroup.Coffee.System.features.user.domain.dto;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        @Email(message = "El email no es válido")
        String email,
        Boolean state
        ) {
}
