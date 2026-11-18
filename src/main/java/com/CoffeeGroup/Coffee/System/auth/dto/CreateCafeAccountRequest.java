package com.CoffeeGroup.Coffee.System.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCafeAccountRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña debe tener al menos una mayúscula, una minúscula y un número"
        )
        String password,

        @NotBlank(message = "El nombre de la cafetería es obligatorio")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String name,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(min = 5, max = 150, message = "La dirección debe tener entre 5 y 150 caracteres")
        String address,

        @Size(max = 500, message = "La descripción puede tener hasta 500 caracteres")
        String description,

        @Pattern(
                regexp = "^[0-9+\\-\\s()]{6,20}$",
                message = "El teléfono no tiene un formato válido"
        )
        String phoneNumber
) {
}