package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCafeRequest (

        @NotBlank(message = "El nombre de la cafetería es obligatorio")
        String name,

        @NotBlank(message = "La dirección es obligatoria")
        String address,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe tener un formato de email válido")
        String email, // Nota: Aquí pedimos un String. En tu servicio lo convertirás a tu objeto Email

        @Pattern(regexp = "^\\d{10}$", message = "El numero de teléfono debe contener la longitud correcta")
        String phoneNumber,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        String schedule,

        String image
){
}
