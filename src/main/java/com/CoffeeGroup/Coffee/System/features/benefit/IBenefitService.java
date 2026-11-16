package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

public interface IBenefitService {

    BenefitResponse save(BenefitCreateRequest request);

    BenefitResponse update(UUID externalId, BenefitUpdateRequest request);

    void delete(UUID externalId);

    List<BenefitResponse> getAll();

    BenefitResponse findById(UUID externalId);

    BenefitResponse confirmBenefit(UUID externalId);

    BenefitResponse cancelBenefit(UUID externalId);

    BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest request);

    List<BenefitResponse> getAllByCafe(UUID cafeExternalId);

    BenefitResponse getBenefitByCafe(UUID cafeExternalId, UUID benefitExternalId);

    List<BenefitResponse> filterBenefitsByCafe(UUID cafeId, String name, Boolean active, Boolean isPublic, BenefitType type);

    List<BenefitResponse> filterAvailableBenefits(UUID cafeId, String name, BenefitType type, Integer maxPoints);

    List<BenefitResponse> filterAvailableGlobalBenefits(String name, BenefitType type, Integer maxPoints);

    List<BenefitResponse> getAvailableCatalog(UUID cafeId, BenefitType type);
}
