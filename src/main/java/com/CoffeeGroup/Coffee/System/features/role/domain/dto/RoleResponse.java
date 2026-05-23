package com.CoffeeGroup.Coffee.System.features.role.domain.dto;

import java.util.UUID;

public record RoleResponse(
        UUID externalId,
        String name)
{
}
