package com.CoffeeGroup.Coffee.System.features.role.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest (
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "The name must be between 2 and 50 characters")
        String name
){

}
