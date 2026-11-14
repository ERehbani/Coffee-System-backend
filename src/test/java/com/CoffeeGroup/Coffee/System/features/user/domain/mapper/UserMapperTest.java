package com.CoffeeGroup.Coffee.System.features.user.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private final Date creationDate = new Date(1_700_000_000_000L);

    private UserEntity buildUser() {
        return UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(new Email("cliente@test.com"))
                .state(true)
                .creationDate(creationDate)
                .build();
    }

    @Test
    void toResponseIncludesTheCreationDate() {
        UserEntity user = buildUser();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.externalId()).isEqualTo(user.getExternalId());
        assertThat(response.email()).isEqualTo("cliente@test.com");
        assertThat(response.state()).isTrue();
        assertThat(response.creationDate()).isEqualTo(creationDate);
    }

    @Test
    void toResponseWithCustomIdIncludesTheCreationDate() {
        UUID customId = UUID.randomUUID();

        UserResponse response = mapper.toResponse(buildUser(), customId);

        assertThat(response.externalId()).isEqualTo(customId);
        assertThat(response.creationDate()).isEqualTo(creationDate);
    }
}
