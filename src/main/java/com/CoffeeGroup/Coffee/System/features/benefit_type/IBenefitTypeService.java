package com.CoffeeGroup.Coffee.System.features.benefit_type;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;

import java.util.List;
import java.util.UUID;

public interface IBenefitTypeService {
    BenefitTypeResponse save(BenefitTypeCreate benefitTypeCreate);
    BenefitTypeResponse update(UUID externalId, BenefitTypeCreate benefitTypeCreate);
    void delete(UUID externalId);
    List<BenefitTypeResponse> all();
    void validateName(String name);
}
