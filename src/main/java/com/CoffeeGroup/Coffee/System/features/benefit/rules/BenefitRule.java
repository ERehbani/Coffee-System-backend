package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.util.Set;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseDraft;

public interface BenefitRule {

    Set<BenefitType> supports();

    void validate(BenefitEntity benefit);

    void apply(BenefitEntity benefit, PurchaseDraft draft);
}
