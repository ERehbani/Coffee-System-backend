package com.CoffeeGroup.Coffee.System.features.client;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.CreateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    ClientResponse create(CreateClientRequest request);

    List<ClientResponse> findAll();

    ClientResponse getById(UUID externalId);

    ClientResponse update(UUID externalId, UpdateClientRequest request);

    void delete(UUID externalId);
}
