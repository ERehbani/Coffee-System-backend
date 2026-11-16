package com.CoffeeGroup.Coffee.System.features.client.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

class ClientMapperTest {

    private final ClientMapper mapper = new ClientMapper();

    private final LocalDateTime creationDate = LocalDateTime.of(2026, 6, 14, 9, 47);

    private ClientEntity buildClient() {
        UserEntity user = UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(new Email("cliente@test.com"))
                .active(true)
                .createdAt(creationDate)
                .build();
        TierEntity tier = TierEntity.builder().externalId(UUID.randomUUID()).name("PLATA").build();
        return ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .currentPoints(320)
                .historicalPoints(1250)
                .tier(tier)
                .user(user)
                .build();
    }

    @Test
    void toResponseIncludesTheEmailAndCreationDateOfTheUser() {
        ClientResponse response = mapper.toResponse(buildClient());

        assertThat(response.email()).isEqualTo("cliente@test.com");
        assertThat(response.creationDate()).isEqualTo(creationDate);
    }

    @Test
    void toResponseIncludesThePointsAndTier() {
        ClientResponse response = mapper.toResponse(buildClient());

        assertThat(response.currentPoints()).isEqualTo(320);
        assertThat(response.historicalPoints()).isEqualTo(1250);
        assertThat(response.tierName()).isEqualTo("PLATA");
        assertThat(response.state()).isTrue();
    }
}
