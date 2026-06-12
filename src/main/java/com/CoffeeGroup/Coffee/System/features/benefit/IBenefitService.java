package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface IBenefitService {

    BenefitResponse save(BenefitCreateRequest benefitCreate);
    void delete(UUID externalId);
    List<BenefitResponse> findAll();
    BenefitResponse findByBenefit(UUID externalId);
    BenefitResponse confirmBenefit(UUID externalId);
    BenefitResponse cancelBenefit(UUID externalId);
    BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest newEndDateRequest);
    BenefitEntity findBenefit(UUID externalId);
    List<BenefitResponse> getAllByCafe(UUID cafeExternalId);
}
