package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.PointsRangeResponse;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class ProductPointsPolicyTest {

    @Mock
    private CafeProductRepository cafeProductRepository;

    private ProductPointsPolicy policy;
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CatalogProductEntity capuccino = TestData.catalogProduct("Capuccino");

    @BeforeEach
    void setUp() {
        policy = new ProductPointsPolicy(cafeProductRepository, new BigDecimal("25"), 20);
    }

    private void otherCafesSell(String totalPrice, long cafes) {
        when(cafeProductRepository.priceStatsOfOtherCafes(any(), any())).thenReturn(
                new CafeProductRepository.PriceStats() {
                    @Override
                    public BigDecimal getTotal() {
                        return new BigDecimal(totalPrice);
                    }

                    @Override
                    public Long getCafes() {
                        return cafes;
                    }
                });
    }

    @Test
    void theFirstCafeToSellAProductGetsPointsFromItsOwnPrice() {
        otherCafesSell("0", 0);

        PointsRangeResponse range = policy.rangeFor(central, capuccino, new BigDecimal("2500"));

        assertThat(range.suggested()).isEqualTo(100);
        assertThat(range.min()).isEqualTo(80);
        assertThat(range.max()).isEqualTo(120);
        assertThat(range.referencePrice()).isEqualByComparingTo("2500");
        assertThat(range.cafesSelling()).isEqualTo(1);
    }

    @Test
    void theSuggestedPointsComeFromTheAveragePriceOfEveryCafe() {
        otherCafesSell("4000", 1);

        PointsRangeResponse range = policy.rangeFor(central, capuccino, new BigDecimal("2000"));

        assertThat(range.referencePrice()).isEqualByComparingTo("3000");
        assertThat(range.suggested()).isEqualTo(120);
        assertThat(range.min()).isEqualTo(96);
        assertThat(range.max()).isEqualTo(144);
        assertThat(range.cafesSelling()).isEqualTo(2);
    }

    @Test
    void aCafeThatChargesMuchMoreStillAwardsPointsCloseToTheOthers() {
        otherCafesSell("5000", 2);

        PointsRangeResponse range = policy.rangeFor(central, capuccino, new BigDecimal("10000"));

        assertThat(range.suggested()).isEqualTo(200);
        assertThat(range.max()).isLessThan(10000 / 25);
    }

    @Test
    void aProductNewToTheCatalogOnlyLooksAtItsOwnPrice() {
        PointsRangeResponse range = policy.rangeFor(central, null, new BigDecimal("800"));

        assertThat(range.suggested()).isEqualTo(32);
        verifyNoInteractions(cafeProductRepository);
    }

    @Test
    void thePointsNeverGoBelowOne() {
        PointsRangeResponse range = policy.rangeFor(central, null, new BigDecimal("5"));

        assertThat(range.suggested()).isEqualTo(1);
        assertThat(range.min()).isEqualTo(1);
    }

    @Test
    void emptyPointsTakeTheSuggestedOnes() {
        PointsRangeResponse range = new PointsRangeResponse(100, 80, 120, new BigDecimal("2500"), 1);

        assertThat(policy.resolve(null, range)).isEqualTo(100);
        assertThat(policy.resolve(80, range)).isEqualTo(80);
        assertThat(policy.resolve(120, range)).isEqualTo(120);
    }

    @Test
    void pointsBelowTheFloorOrAboveTheCapAreRejected() {
        PointsRangeResponse range = new PointsRangeResponse(100, 80, 120, new BigDecimal("2500"), 1);

        assertThatThrownBy(() -> policy.resolve(79, range)).isInstanceOf(BusinessRuleException.class)
                .hasMessage("Awarded points for this product must be between 80 and 120");
        assertThatThrownBy(() -> policy.resolve(121, range)).isInstanceOf(BusinessRuleException.class);
    }
}
