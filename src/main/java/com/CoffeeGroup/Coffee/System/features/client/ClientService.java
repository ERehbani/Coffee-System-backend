package com.CoffeeGroup.Coffee.System.features.client;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.mapper.ClientMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {
    private final ClientRepository clientRepository;
    private final TierRepository tierRepository;
    private final UserRepository userRepository;
    private final ICartRepository cartRepository;
    private final ClientMapper clientMapper;



    @Transactional
    public ClientEntity create(String name, UserEntity user) {

        TierEntity initialTier = tierRepository.findByMinPoints(0)
                .orElseThrow(() -> new EntityNotFoundException("Initial tier not found"));

        ClientEntity client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .profilePhoto(null)
                .currentPoints(0)
                .historicalPoints(0)
                .tier(initialTier)
                .user(user)
                .build();

        ClientEntity clientSaved = clientRepository.save(client);
        clientRepository.save(clientSaved);

        return clientSaved;
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    public ClientResponse getById(UUID externalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        ClientEntity client;
        if (isAdmin) {
            client = clientRepository.findByExternalId(externalId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        } else {
            client = clientRepository.findByExternalIdAndUser_Credentials_Username(externalId, auth.getName())
                    .orElseThrow(() -> new AccessDeniedException("Access denied"));
        }
        return clientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponse update(UUID externalId, UpdateClientRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        ClientEntity client;
        if (isAdmin) {
            client = clientRepository.findByExternalId(externalId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        } else {
            String username = auth.getName();
            client = clientRepository.findByExternalIdAndUser_Credentials_Username(externalId, username)
                    .orElse(null);
            
            if (client == null) {
                // Try to find client by externalId first to see if it exists
                var clientById = clientRepository.findByExternalId(externalId);
                if (clientById.isPresent()) {
                    throw new AccessDeniedException("Access denied: You can only update your own client data");
                }
                throw new EntityNotFoundException("Client not found with externalId: " + externalId);
            }
        }

        if (request.name() != null) {
            client.setName(request.name());
        }

        if (request.profilePhoto() != null) {
            client.setProfilePhoto(request.profilePhoto());
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

    @Override
    public ClientPointsResponse viewMyPoints() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        ClientEntity client = clientRepository.findByUser_Credentials_Username(username)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        return clientMapper.toResponsePoints(client);
    }
}