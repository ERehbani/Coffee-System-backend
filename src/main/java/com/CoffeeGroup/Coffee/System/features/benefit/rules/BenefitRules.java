package com.CoffeeGroup.Coffee.System.features.benefit.rules;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

@Component
public class BenefitRules {

    private final Map<BenefitType, BenefitRule> rulesByType = new EnumMap<>(BenefitType.class);

    public BenefitRules(List<BenefitRule> rules) {
        for (BenefitRule rule : rules) {
            rule.supports().forEach(type -> rulesByType.put(type, rule));
        }
    }

    public BenefitRule ruleFor(BenefitEntity benefit) {
        BenefitRule rule = rulesByType.get(benefit.type());
        if (rule == null) {
            throw new BusinessRuleException("The benefit type " + benefit.type() + " cannot be applied");
        }
        return rule;
    }
}
