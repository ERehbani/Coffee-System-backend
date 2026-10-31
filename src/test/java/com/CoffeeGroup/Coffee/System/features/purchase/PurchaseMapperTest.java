package com.CoffeeGroup.Coffee.System.features.purchase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper.PurchaseDetailMapper;
import com.CoffeeGroup.Coffee.System.features.review.IReviewRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseMapperTest {

    @Mock
    private IReviewRepository reviewRepository;

    private String clientNameOf(String name, String lastName) {
        PurchaseEntity purchase = PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .client(ClientEntity.builder().externalId(UUID.randomUUID()).name(name).lastName(lastName).build())
                .cafe(CafeEntity.builder().externalId(UUID.randomUUID()).name("Cafe Central").build())
                .build();
        return new PurchaseMapper(new PurchaseDetailMapper(), reviewRepository).toResponse(purchase).clientName();
    }

    @Test
    void theClientNameJoinsTheNameAndTheLastName() {
        assertThat(clientNameOf("Ana", "Perez")).isEqualTo("Ana Perez");
    }

    @Test
    void theClientNameIsTheNameAloneWhenThereIsNoLastName() {
        assertThat(clientNameOf("Ana", null)).isEqualTo("Ana");
        assertThat(clientNameOf(" Ana ", "  ")).isEqualTo("Ana");
    }

    @Test
    void theClientNameIsNullWhenTheClientHasNoName() {
        assertThat(clientNameOf(null, null)).isNull();
    }
}
