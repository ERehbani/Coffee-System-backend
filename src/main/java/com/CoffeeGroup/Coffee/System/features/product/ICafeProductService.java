package com.CoffeeGroup.Coffee.System.features.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductFilter;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

public interface ICafeProductService {

    PageResponse<ProductResponse> listByCafe(UUID cafeExternalId, ProductFilter filter, Pageable pageable);

    List<ProductResponse> listVisibleToCurrentUser();

    ProductResponse getById(UUID productExternalId);

    ProductResponse create(UUID cafeExternalId, ProductRequest request);

    ProductResponse createForCurrentCafe(ProductRequest request);

    ProductResponse update(UUID productExternalId, UpdateProductRequest request);

    ProductResponse changeAvailability(UUID productExternalId, boolean available);

    void deactivate(UUID productExternalId);

    ProductResponse activate(UUID productExternalId);
}
