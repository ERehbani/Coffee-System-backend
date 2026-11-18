package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.util.function.Predicate;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;

final class ProductTargetRules {

    private ProductTargetRules() {
    }

    static void requireTargetProduct(BenefitEntity benefit) {
        if (benefit.isGlobal() && benefit.getCatalogProduct() == null) {
            throw new BusinessRuleException("Un beneficio global de producto necesita un producto del catálogo");
        }
        if (!benefit.isGlobal() && benefit.getCafeProduct() == null) {
            throw new BusinessRuleException("Un beneficio de producto necesita un producto de la cafetería");
        }
    }

    static Predicate<CafeProductEntity> targetOf(BenefitEntity benefit) {
        if (benefit.getCafeProduct() != null) {
            return product -> product.getExternalId().equals(benefit.getCafeProduct().getExternalId());
        }
        return product -> product.getCatalogProduct().getExternalId()
                .equals(benefit.getCatalogProduct().getExternalId());
    }
}
