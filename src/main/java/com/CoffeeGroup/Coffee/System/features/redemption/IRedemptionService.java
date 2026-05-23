package com.CoffeeGroup.Coffee.System.features.redemption;

import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionUpdateRequest;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

import java.util.List;

public interface IRedemptionService {

    RedemptionResponse save(RedemptionCreateRequest redemptionCreateRequest);

    RedemptionResponse update(UUID externalId , RedemptionUpdateRequest redemptionUpdateRequest);

    RedemptionResponse findByid(UUID externalId);

    void deleteByid(UUID id);

    List<RedemptionResponse> findAll();

}
