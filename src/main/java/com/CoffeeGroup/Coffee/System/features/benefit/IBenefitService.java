package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreate;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface IBenefitService {

    BenefitResponse save(BenefitCreate benefitCreate);
    void delete(UUID externalId);
    List<BenefitResponse>all();
    List<BenefitResponse>shareByPointCost(Integer pointCost);
    List<BenefitResponse>shareByEndDate(Date date);
    List<BenefitResponse>shareByStatus(Boolean status);
}
