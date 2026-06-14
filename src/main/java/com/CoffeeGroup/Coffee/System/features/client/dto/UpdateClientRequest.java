package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.UUID;



public record UpdateClientRequest (
        String name,

        String profilePhoto,

        UUID tierExternalId,

        UUID userExternalId
){
}
