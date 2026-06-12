package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

public interface ICafeService {

    List<CafeResponse> getAll();

    CafeResponse getById(UUID externalId);

    CafeEntity create(CreateCafeAccountRequest request, UserEntity user);

    CafeResponse update(UUID externalId, UpdateCafeRequest request);

    void delete(UUID externalId);

    List<CafeResponse> filterCafes(String name, String address, Boolean state);

}
