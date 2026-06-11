package com.CoffeeGroup.Coffee.System.features.client.dto;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public record ClientPointsResponse (
        String name,
        Integer currentPoints
){
}
