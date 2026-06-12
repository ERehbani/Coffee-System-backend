package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;

public interface IBenefitService {

    BenefitResponse save(BenefitCreateRequest benefitCreate);
    void delete(UUID externalId);
    List<BenefitResponse> getAll();
    BenefitResponse getByBenefit(UUID externalId);
    BenefitResponse confirmBenefit(UUID externalId);
    BenefitResponse cancelBenefit(UUID externalId);
    BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest newEndDateRequest);
    BenefitEntity getBenefit(UUID externalId);
    List<BenefitResponse> getAllByCafe(UUID cafeExternalId);
    BenefitResponse getBenefitByCafe(UUID cafeExternalId, UUID benefitExternalId);
}
