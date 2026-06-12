package com.CoffeeGroup.Coffee.System.features.user.domain.dto;

import java.util.UUID;

public record UserResponse (
        UUID externalId,
        String email,
        Boolean state
){
}
