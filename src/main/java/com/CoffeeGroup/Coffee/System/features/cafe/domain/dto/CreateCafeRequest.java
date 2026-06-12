package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCafeRequest (

        @NotBlank(message = "The name is required")
        String name,

        @NotBlank(message = "The address is required")
        String address,

        @NotBlank(message = "The email is required")
        @Email(message = "The email must be in a valid format.")
        String email,

        @Pattern(regexp = "^\\d{10}$", message = "The phone number must be of the correct length.")
        String phoneNumber,

        @Size(max = 500, message = "The description cannot exceed 500 characters.")
        String description,

        String schedule,

        String image
){
}
