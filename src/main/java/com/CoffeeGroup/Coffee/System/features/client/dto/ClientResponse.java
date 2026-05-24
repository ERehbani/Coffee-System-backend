package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.UUID;

public record ClientResponse (
        UUID externalId,
        String name,
        String profilePhoto,
        Integer currentPoints,
        Integer historicalPoints,
        UUID tierExternalId,
        String tierName,
        UUID userExternalId,
        Boolean state
){
}
