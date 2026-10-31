package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.mapper.ClientMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ICartRepository cartRepository;

    private ClientService service;
    private ClientEntity client;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))));
        service = new ClientService(clientRepository, tierRepository, userRepository, cartRepository, new ClientMapper());

        client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .lastName("Perez")
                .phone("1155550000")
                .tier(TierEntity.builder().externalId(UUID.randomUUID()).name("BRONCE").minPoints(0).build())
                .user(UserEntity.builder().externalId(UUID.randomUUID()).state(true).build())
                .build();
        when(clientRepository.findByExternalId(client.getExternalId())).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateStoresAndExposesTheLastNameAndPhone() {
        ClientResponse response = service.update(client.getExternalId(),
                new UpdateClientRequest(null, "Gomez", "1144440000", null));

        assertThat(client.getLastName()).isEqualTo("Gomez");
        assertThat(client.getPhone()).isEqualTo("1144440000");
        assertThat(response.name()).isEqualTo("Ana");
        assertThat(response.lastName()).isEqualTo("Gomez");
        assertThat(response.phone()).isEqualTo("1144440000");
    }

    @Test
    void updateKeepsTheLastNameAndPhoneWhenTheyAreNotSent() {
        ClientResponse response = service.update(client.getExternalId(),
                new UpdateClientRequest("Ana Maria", null, null, null));

        assertThat(response.name()).isEqualTo("Ana Maria");
        assertThat(response.lastName()).isEqualTo("Perez");
        assertThat(response.phone()).isEqualTo("1155550000");
    }
}
