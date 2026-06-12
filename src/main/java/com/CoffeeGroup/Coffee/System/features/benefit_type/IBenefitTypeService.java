package com.CoffeeGroup.Coffee.System.features.benefit_type;

import java.util.List;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;

public interface IBenefitTypeService {
    List<BenefitTypeResponse> getAll();
    BenefitTypeResponse create(BenefitTypeCreate benefitTypeCreate);
}
