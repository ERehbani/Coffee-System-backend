package com.CoffeeGroup.Coffee.System.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 6, message = "La contraseña nueva debe tener al menos 6 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "La contraseña nueva debe tener al menos una mayúscula, una minúscula y un número"
        )
        String newPassword
) {
}
