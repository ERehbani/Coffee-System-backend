package com.CoffeeGroup.Coffee.System.features.cafe;

import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ICafeService {

    List<CafeResponse> getAll();

    CafeResponse getById(UUID externalId);

    CafeResponse create(CreateCafeRequest request);

    CafeResponse update(UUID externalId, UpdateCafeRequest request);

    void delete(UUID externalId);

}
