package com.CoffeeGroup.Coffee.System.features.client;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.CreateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.mapper.ClientMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ClientService {
    private final IClientRepository clientRepository;
    private final TierRepository tierRepository;
    private final UserRepository userRepository;
    private final ICartRepository cartRepository;
    private final ClientMapper clientMapper;


    @Transactional
    public ClientResponse create(CreateClientRequest request) {

        TierEntity tier = tierRepository.findByExternalId(request.tierExternalId())
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));

        UserEntity user = userRepository.findByExternalId(request.userExternalId()  )
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ClientEntity client = clientMapper.toEntity(request, tier, user);

        ClientEntity clientSaved = clientRepository.save(client);

        CartEntity cart = CartEntity.builder()
                .externalId(UUID.randomUUID())
                .clientId(clientSaved.getExternalId())
                .items(List.of())
                .totalPrice(BigDecimal.ZERO)
                .build();
        cartRepository.save(cart);

        return clientMapper.toResponse(clientSaved);
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public ClientResponse getById(UUID externalId) {

        ClientEntity client = clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponse update(UUID externalId, UpdateClientRequest request) {

        ClientEntity client = clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        if (request.name() != null) {
            client.setName(request.name());
        }

        if (request.profilePhoto() != null) {
            client.setProfilePhoto(request.profilePhoto());
        }

        if (request.currentPoints() != null) {
            client.setCurrentPoints(request.currentPoints());
        }

        if (request.historicalPoints() != null) {
            client.setHistoricalPoints(request.historicalPoints());
        }

        if (request.tierExternalId() != null) {
            TierEntity tier = tierRepository.findByExternalId(request.tierExternalId())
                    .orElseThrow(() -> new EntityNotFoundException("Tier not found"));

            client.setTier(tier);
        }

        if (request.userExternalId() != null) {
            UserEntity user = userRepository.findByExternalId(request.userExternalId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            client.setUser(user);
        }

        ClientEntity clientUpdated = clientRepository.save(client);

        return clientMapper.toResponse(clientUpdated);
    }

    @Transactional
    public void delete(UUID externalId) {
        ClientEntity client = clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));


        client.getUser().setState(false);
        clientRepository.save(client);
    }
}