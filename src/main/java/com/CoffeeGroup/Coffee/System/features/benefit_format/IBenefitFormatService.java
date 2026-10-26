package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;

public interface IBenefitFormatService {
    List<BenefitFormatResponse> getAll();

    BenefitFormatResponse create(CreateBenefitFormatRequest request);

    BenefitFormatResponse update(UUID externalId, UpdateBenefitFormatRequest request);
}
