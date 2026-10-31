package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.UUID;



public record UpdateClientRequest (
        String name,

        String lastName,

        String phone,

        String profilePhoto
){
}
