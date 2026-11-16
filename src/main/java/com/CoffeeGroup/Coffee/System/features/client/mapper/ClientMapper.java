package com.CoffeeGroup.Coffee.System.features.client.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;

@Component
public class ClientMapper {

    public ClientResponse toResponse(ClientEntity client) {
        return new ClientResponse(
                client.getExternalId(),
                client.getName(),
                client.getLastName(),
                client.getPhone(),
                client.getProfilePhoto(),
                client.getCurrentPoints(),
                client.getHistoricalPoints(),
                client.getTier().getExternalId(),
                client.getTier().getName(),
                client.getUser().getExternalId(),
                client.getUser().isActive(),
                client.email(),
                client.getUser().getCreatedAt());
    }

    public ClientPointsResponse toPointsResponse(ClientEntity client) {
        return new ClientPointsResponse(client.getName(), client.getCurrentPoints());
    }
}
