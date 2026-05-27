package com.CoffeeGroup.Coffee.System.features.product;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(UUID productId);
    ProductResponse save(ProductRequest productRequest);

    ProductResponse update(ProductRequest productResponse, UUID productId);
    void delete(UUID productId);
}
