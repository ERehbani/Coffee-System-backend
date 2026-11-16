package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseDraft;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class BenefitRulesTest {

    private final CafeProductRepository cafeProductRepository = mock(CafeProductRepository.class);
    private final BenefitRules rules = new BenefitRules(List.of(
            new PercentageDiscountRule(), new TwoForOneRule(), new FreeProductRule(cafeProductRepository)));

    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CatalogProductEntity medialunaCatalog = TestData.catalogProduct("Medialuna");
    private final CafeProductEntity medialuna = TestData.product(central, medialunaCatalog, "800", 50);
    private final CafeProductEntity capuccino = TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);

    private PurchaseDraft draftWith(CafeProductEntity product, int quantity) {
        CartEntity cart = CartEntity.openFor(TestData.client(bronce, 0), central);
        cart.add(product, quantity);
        return PurchaseDraft.from(cart);
    }

    private BenefitEntity benefit(BenefitType type, CafeEntity cafe) {
        return TestData.benefit("Beneficio", type, cafe, bronce);
    }

    @Test
    void everyBenefitTypeHasARule() {
        for (BenefitType type : BenefitType.values()) {
            assertThat(rules.ruleFor(benefit(type, central))).isNotNull();
        }
    }

    @Test
    void percentageDiscountReducesTheTotal() {
        BenefitEntity discount = benefit(BenefitType.CODIGO_DESCUENTO, null);
        discount.setDiscountPercentage(new BigDecimal("15"));
        PurchaseDraft draft = draftWith(capuccino, 2);

        rules.ruleFor(discount).apply(discount, draft);

        assertThat(draft.total()).isEqualByComparingTo("4250.00");
    }

    @Test
    void percentageDiscountNeedsAValidPercentage() {
        BenefitEntity discount = benefit(BenefitType.CODIGO_DESCUENTO, null);

        assertThatThrownBy(() -> rules.ruleFor(discount).validate(discount)).isInstanceOf(BusinessRuleException.class);

        discount.setDiscountPercentage(new BigDecimal("120"));
        assertThatThrownBy(() -> rules.ruleFor(discount).validate(discount)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void twoForOneMakesEverySecondUnitFree() {
        BenefitEntity twoForOne = benefit(BenefitType.DOS_POR_UNO, central);
        twoForOne.setCafeProduct(medialuna);
        PurchaseDraft draft = draftWith(medialuna, 5);

        rules.ruleFor(twoForOne).apply(twoForOne, draft);

        assertThat(draft.total()).isEqualByComparingTo("2400.00");
    }

    @Test
    void aGlobalTwoForOneMatchesTheCafeProductThroughTheCatalog() {
        BenefitEntity global = benefit(BenefitType.DOS_POR_UNO, null);
        global.setCatalogProduct(medialunaCatalog);
        PurchaseDraft draft = draftWith(medialuna, 2);

        rules.ruleFor(global).apply(global, draft);

        assertThat(draft.total()).isEqualByComparingTo("800.00");
    }

    @Test
    void twoForOneWithoutTwoUnitsDoesNotWasteTheCoupon() {
        BenefitEntity twoForOne = benefit(BenefitType.DOS_POR_UNO, central);
        twoForOne.setCafeProduct(medialuna);

        assertThatThrownBy(() -> rules.ruleFor(twoForOne).apply(twoForOne, draftWith(medialuna, 1)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void freeProductAddsTheGiftWithoutPriceOrPoints() {
        BenefitEntity free = benefit(BenefitType.PRODUCTO_POR_PUNTOS, central);
        free.setCafeProduct(medialuna);
        PurchaseDraft draft = draftWith(capuccino, 1);

        rules.ruleFor(free).apply(free, draft);

        assertThat(draft.total()).isEqualByComparingTo("2500.00");
        PurchaseDetailEntity gift = draft.details().get(1);
        assertThat(gift.getProduct()).isSameAs(medialuna);
        assertThat(gift.getUnitPrice()).isEqualByComparingTo("0");
        assertThat(gift.getGeneratedPoints()).isZero();
    }

    @SuppressWarnings("unchecked")
    @Test
    void aGlobalFreeProductFailsWhenTheCafeDoesNotSellIt() {
        BenefitEntity global = benefit(BenefitType.PRODUCTO_GRATIS_SOCIO, null);
        global.setCatalogProduct(TestData.catalogProduct("Alfajor"));
        when(cafeProductRepository.findAll(any(Specification.class))).thenReturn(List.of(capuccino, medialuna));

        assertThatThrownBy(() -> rules.ruleFor(global).apply(global, draftWith(capuccino, 1)))
                .isInstanceOf(ProductNotAvailableException.class);
    }

    @Test
    void productBenefitsNeedTheRightKindOfProduct() {
        BenefitEntity cafeBenefit = benefit(BenefitType.PRODUCTO_POR_PUNTOS, central);
        BenefitEntity globalBenefit = benefit(BenefitType.DOS_POR_UNO, null);

        assertThatThrownBy(() -> rules.ruleFor(cafeBenefit).validate(cafeBenefit)).isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> rules.ruleFor(globalBenefit).validate(globalBenefit)).isInstanceOf(BusinessRuleException.class);

        cafeBenefit.setCafeProduct(medialuna);
        globalBenefit.setCatalogProduct(medialunaCatalog);
        assertThatCode(() -> rules.ruleFor(cafeBenefit).validate(cafeBenefit)).doesNotThrowAnyException();
        assertThatCode(() -> rules.ruleFor(globalBenefit).validate(globalBenefit)).doesNotThrowAnyException();
    }
}
