package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

public interface IRedemptionService {

    RedemptionResponse create(RedemptionCreateRequest request);

    PageResponse<RedemptionResponse> findAll(RedemptionStatus status, Pageable pageable);

    RedemptionResponse getById(UUID externalId);

    RedemptionResponse validate(String validationCode);

    RedemptionResponse cancel(String validationCode);

    List<RedemptionResponse> getAllByClient(UUID clientExternalId);
}
