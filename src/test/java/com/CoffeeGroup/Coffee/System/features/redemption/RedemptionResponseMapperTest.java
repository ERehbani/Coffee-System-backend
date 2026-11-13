package com.CoffeeGroup.Coffee.System.features.redemption;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionResponseMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionResponseMapperImpl;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

class RedemptionResponseMapperTest {

    private final IRedemptionResponseMapper mapper = new IRedemptionResponseMapperImpl();

    private static RedemptionEntity redemptionOf(BenefitTypeEntity type, CafeEntity cafe) {
        BenefitEntity benefit = BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Coupon")
                .benefitType(type)
                .cafeEntity(cafe)
                .build();
        return RedemptionEntity.builder()
                .externalId(UUID.randomUUID())
                .validationCode("code")
                .state(true)
                .benefit(benefit)
                .cafeEntity(cafe)
                .build();
    }

    @Test
    void toDtoExposesTheDiscountPercentageOfADiscountCoupon() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).name("Central").build();
        BenefitTypeEntity type = BenefitTypeEntity.builder()
                .name(BenefitType.CODIGO_DESCUENTO)
                .porcentajeDescuento(15.0)
                .build();

        RedemptionResponse response = mapper.toDto(redemptionOf(type, cafe));

        assertThat(response.benefitType()).isEqualTo(BenefitType.CODIGO_DESCUENTO);
        assertThat(response.discountPercentage()).isEqualTo(15.0);
        assertThat(response.cafeId()).isEqualTo(cafe.getExternalId());
        assertThat(response.cafeName()).isEqualTo("Central");
        assertThat(response.productId()).isNull();
        assertThat(response.productName()).isNull();
        assertThat(response.productPrice()).isNull();
    }

    @Test
    void toDtoExposesTheProductOfAProductCoupon() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).name("Central").build();
        ProductEntity product = ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Cappuccino")
                .price(new BigDecimal("1500"))
                .build();
        BenefitTypeEntity type = BenefitTypeEntity.builder()
                .name(BenefitType.DOS_POR_UNO)
                .product(product)
                .build();

        RedemptionResponse response = mapper.toDto(redemptionOf(type, cafe));

        assertThat(response.benefitType()).isEqualTo(BenefitType.DOS_POR_UNO);
        assertThat(response.productId()).isEqualTo(product.getExternalId());
        assertThat(response.productName()).isEqualTo("Cappuccino");
        assertThat(response.productPrice()).isEqualByComparingTo("1500");
        assertThat(response.discountPercentage()).isNull();
    }

    @Test
    void toDtoReturnsNullCafeIdForAGlobalCoupon() {
        BenefitTypeEntity type = BenefitTypeEntity.builder()
                .name(BenefitType.CODIGO_DESCUENTO)
                .porcentajeDescuento(10.0)
                .build();

        RedemptionResponse response = mapper.toDto(redemptionOf(type, null));

        assertThat(response.cafeId()).isNull();
        assertThat(response.cafeName()).isEqualTo("Global");
    }

    @Test
    void toDtoReturnsNullBenefitFieldsWhenTheBenefitHasNoType() {
        RedemptionResponse response = mapper.toDto(redemptionOf(null, null));

        assertThat(response.benefitType()).isNull();
        assertThat(response.discountPercentage()).isNull();
        assertThat(response.productId()).isNull();
    }
}
