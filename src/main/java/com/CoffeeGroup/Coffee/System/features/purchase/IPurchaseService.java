package com.CoffeeGroup.Coffee.System.features.purchase;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.CreateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface IPurchaseService {
    PurchaseResponse create(CreatePurchaseRequest request);
    List<PurchaseResponse> findAll();
    PurchaseResponse getById(UUID externalId);
    PurchaseResponse update(UUID externalId, UpdatePurchaseRequest request);
    void delete(UUID externalId);
}
