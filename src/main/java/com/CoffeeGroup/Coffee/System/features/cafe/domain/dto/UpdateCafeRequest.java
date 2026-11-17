package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCafeRequest(
        @NotBlank(message = "La dirección no puede estar vacía")
        String address,

        @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
        String phoneNumber,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        @Size(max = 500, message = "El logo no puede superar los 500 caracteres")
        String logo,

        @Size(max = 500, message = "La imagen no puede superar los 500 caracteres")
        String image,

        List<@Valid OpeningHoursRequest> openingHours
) {
}
