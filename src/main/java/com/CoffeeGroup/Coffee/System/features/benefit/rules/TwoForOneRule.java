package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseDraft;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;

@Component
public class TwoForOneRule implements BenefitRule {

    @Override
    public Set<BenefitType> supports() {
        return Set.of(BenefitType.DOS_POR_UNO);
    }

    @Override
    public void validate(BenefitEntity benefit) {
        ProductTargetRules.requireTargetProduct(benefit);
    }

    @Override
    public void apply(BenefitEntity benefit, PurchaseDraft draft) {
        Predicate<CafeProductEntity> target = ProductTargetRules.targetOf(benefit);
        int freeUnits = draft.unitsOf(target) / 2;
        if (freeUnits == 0) {
            throw new BusinessRuleException("The 2x1 needs at least two units of the product in the cart");
        }
        BigDecimal unitPrice = draft.details().stream()
                .map(PurchaseDetailEntity::getProduct)
                .filter(target)
                .findFirst()
                .map(CafeProductEntity::getPrice)
                .orElse(BigDecimal.ZERO);
        draft.subtract(unitPrice.multiply(BigDecimal.valueOf(freeUnits)));
    }
}
