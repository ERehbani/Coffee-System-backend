package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseDraft;

@Component
public class PercentageDiscountRule implements BenefitRule {

    private static final BigDecimal MAX_PERCENTAGE = BigDecimal.valueOf(100);

    @Override
    public Set<BenefitType> supports() {
        return Set.of(BenefitType.CODIGO_DESCUENTO);
    }

    @Override
    public void validate(BenefitEntity benefit) {
        BigDecimal percentage = benefit.getDiscountPercentage();
        if (percentage == null || percentage.signum() <= 0 || percentage.compareTo(MAX_PERCENTAGE) > 0) {
            throw new BusinessRuleException("A discount benefit needs a percentage between 0 and 100");
        }
    }

    @Override
    public void apply(BenefitEntity benefit, PurchaseDraft draft) {
        draft.applyPercentageDiscount(benefit.getDiscountPercentage());
    }
}
