package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateAccountRequest;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import org.apache.catalina.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<UserResponse> getAll();
    UserResponse getById(UUID externalId);
    UserResponse update(UUID externalId, UpdateUserRequest request);
    void delete(UUID externalId);
    UserResponse saveCafe(CreateCafeAccountRequest request);
    UserResponse save(CreateAccountRequest request);
}
