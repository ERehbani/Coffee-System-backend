package com.CoffeeGroup.Coffee.System.features.client;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IClientService {
    ClientEntity create(String name, UserEntity user);

    List<ClientResponse> findAll();

    ClientResponse getById(UUID externalId);

    ClientResponse update(UUID externalId, UpdateClientRequest request);

    void delete(UUID externalId);
}
