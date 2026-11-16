package com.CoffeeGroup.Coffee.System.features.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

public interface IUserService {

    PageResponse<UserResponse> getAll(Pageable pageable);

    UserResponse getById(UUID externalId);

    UserResponse update(UUID externalId, UpdateUserRequest request);

    void delete(UUID externalId);

    UserResponse saveCafe(CreateCafeAccountRequest request);

    UserResponse save(CreateAccountRequest request);
}
