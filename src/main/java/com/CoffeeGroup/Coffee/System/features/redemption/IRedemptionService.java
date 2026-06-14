package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

public interface IRedemptionService {

    RedemptionResponse create(RedemptionCreateRequest redemptionCreateRequest);

    void deleteById(UUID id);

    List<RedemptionResponse> findAll(UUID client);

    RedemptionResponse validate(String validationCode);

    RedemptionResponse cancel(String validationCode);

    List<RedemptionResponse> getAllByClient(UUID clientExternalId);

}
