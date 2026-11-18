package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductSpecifications;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseDraft;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreeProductRule implements BenefitRule {

    private final CafeProductRepository cafeProductRepository;

    @Override
    public Set<BenefitType> supports() {
        return Set.of(BenefitType.PRODUCTO_POR_PUNTOS, BenefitType.PRODUCTO_GRATIS_SOCIO);
    }

    @Override
    public void validate(BenefitEntity benefit) {
        ProductTargetRules.requireTargetProduct(benefit);
    }

    @Override
    public void apply(BenefitEntity benefit, PurchaseDraft draft) {
        draft.addGift(productToGive(benefit, draft));
    }

    private CafeProductEntity productToGive(BenefitEntity benefit, PurchaseDraft draft) {
        if (benefit.getCafeProduct() != null) {
            return benefit.getCafeProduct();
        }
        return cafeProductRepository.findAll(Specifications.allOf(
                        CafeProductSpecifications.belongsTo(draft.cafe()),
                        CafeProductSpecifications.onlyActive(true)))
                .stream()
                .filter(ProductTargetRules.targetOf(benefit))
                .findFirst()
                .orElseThrow(() -> new ProductNotAvailableException(
                        "Esta cafetería no vende " + benefit.getCatalogProduct().getName()));
    }
}
