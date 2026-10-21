package com.CoffeeGroup.Coffee.System.auth.dto;

import java.util.UUID;

/**
 * Who is logged in. Only one of clientId / cafeId is set, depending on the role.
 */
public record MeResponse(
        UUID userId,
        String email,
        String role,
        String name,
        UUID clientId,
        UUID cafeId
) {
}
