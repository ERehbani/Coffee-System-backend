package com.CoffeeGroup.Coffee.System.features.product;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService implements IProductService{
    private final IProductRepository productRepository;
    private final IMapper<ProductEntity, ProductResponse> productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponse getProductById(UUID productId) {
        return productRepository.findByExternalId(productId)
                .map(productMapper::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product", "Product was not found", "externalId", productId.toString()
                        ));
    }

    @Override
    public ProductResponse save(ProductRequest productRequest) {
        ProductEntity entity = productMapper.toEntity(new ProductResponse(
                UUID.randomUUID(),
                productRequest.name(),
                productRequest.description(),
                productRequest.price(),
                productRequest.awardedPoints()
        ));
        return productMapper.toDto(productRepository.save(entity));
    }

    @Override
    public ProductResponse update(ProductRequest requestBody, UUID productId) {
        ProductEntity product = productRepository.findByExternalId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", "Product not found", "productId", productId));

        if(requestBody.name() != null && !requestBody.name().isBlank()) product.setName(requestBody.name());
        if(requestBody.description() != null && !requestBody.description().isBlank()) product.setDescription(requestBody.description());
        if(requestBody.price() != null) product.setPrice(requestBody.price());
        if (requestBody.awardedPoints() != null && requestBody.awardedPoints() >= 50) product.setAwardedPoints(requestBody.awardedPoints());
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public void delete(UUID productId) {
        ProductEntity product = productRepository.findByExternalId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", "Product not found", "productId", productId));
        productRepository.delete(product);
    }
}
