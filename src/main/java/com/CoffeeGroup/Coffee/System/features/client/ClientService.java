package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.mapper.ClientMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;
    private final TierRepository tierRepository;
    private final ClientMapper clientMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ClientEntity create(String name, UserEntity user) {
        TierEntity initialTier = tierRepository.findByMinPoints(0)
                .orElseThrow(() -> new EntityNotFoundException("Initial tier not found"));
        ClientEntity client = ClientEntity.builder().name(name).tier(initialTier).user(user).build();
        clientRepository.save(client);
        return client;
    }

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream().map(clientMapper::toResponse).toList();
    }

    @Override
    public ClientResponse getById(UUID externalId) {
        return clientMapper.toResponse(accessibleClient(externalId));
    }

    @Override
    @Transactional
    public ClientResponse update(UUID externalId, UpdateClientRequest request) {
        ClientEntity client = accessibleClient(externalId);
        if (request.name() != null) {
            client.setName(request.name());
        }
        if (request.lastName() != null) {
            client.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            client.setPhone(request.phone());
        }
        if (request.profilePhoto() != null) {
            client.setProfilePhoto(request.profilePhoto());
        }
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        findClient(externalId).getUser().deactivate();
    }

    @Override
    public ClientPointsResponse viewMyPoints() {
        return clientMapper.toPointsResponse(securityUtils.currentClient());
    }

    private ClientEntity accessibleClient(UUID externalId) {
        ClientEntity client = findClient(externalId);
        if (!securityUtils.isAdmin() && !client.isSameAs(securityUtils.currentClient())) {
            throw new AccessDeniedException("You can only access your own client data");
        }
        return client;
    }

    private ClientEntity findClient(UUID externalId) {
        return clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }
}
