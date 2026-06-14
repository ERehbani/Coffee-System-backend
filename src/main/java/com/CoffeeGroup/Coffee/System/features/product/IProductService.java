package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

public interface IProductService {
    List<ProductResponse> getAll();

    ProductResponse getById(UUID productExternalId);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(ProductRequest productResponse, UUID productId);

    void delete(UUID productId);

    List<ProductResponse> getAllByCafe(UUID cafeExternalId);

    List<ProductResponse>filterProductsByCafe(UUID cafeExternalId, String name, BigDecimal price, Integer awardedPoints);

}
