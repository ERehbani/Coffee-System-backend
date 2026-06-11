package com.CoffeeGroup.Coffee.System.features.client.mapper;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.CreateClientRequest;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientMapper {

    public ClientEntity toEntity(CreateClientRequest request, TierEntity tier, UserEntity user) {
        return ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name(request.name())
                .profilePhoto(request.profilePhoto())
                .currentPoints(request.currentPoints())
                .historicalPoints(request.historicalPoints())
                .tier(tier)
                .user(user)
                .build();
    }

    public ClientResponse toResponse(ClientEntity client) {
        return new ClientResponse(
                client.getExternalId(),
                client.getName(),
                client.getProfilePhoto(),
                client.getCurrentPoints(),
                client.getHistoricalPoints(),
                client.getTier().getExternalId(),
                client.getTier().getName(),
                client.getUser().getExternalId(),
                client.getUser().getState()

        );
    }

    public ClientPointsResponse toResponsePoints(ClientEntity client) {
        return new ClientPointsResponse(client.getName(), client.getCurrentPoints());
    }
}