package com.CoffeeGroup.Coffee.System.features.benefit_format;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

/** How many benefits use a given format code. */
public record BenefitFormatUsage(BenefitType code, Long total) {
}
