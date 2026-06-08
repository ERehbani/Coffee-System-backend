package com.CoffeeGroup.Coffee.System.features.product;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeService;
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
    private final CafeRepository cafeRepository;

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponse getById(UUID externalId, UUID Cafe) {
        CafeEntity cafe = cafeRepository.findByExternalId(Cafe)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

        return productRepository.findByExternalIdAndCafe(externalId,cafe)
                .map(productMapper::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product", "Product was not found", "externalId", externalId.toString()
                        ));
    }

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        CafeEntity cafe = cafeRepository.findByExternalId(productRequest.cafe())
                .orElseThrow(() -> new EntityNotFoundException("Cafe no encontrado"));
        ProductEntity entity = ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .awardedPoints(productRequest.awardedPoints())
                .state(true)
                .cafe(cafe)
                .build();
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
        ProductEntity entity = productRepository.findByExternalId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", "Product not found", "productId", productId));

        productRepository.delete(entity);

    }

    @Override
    public List<ProductResponse> ListProducts(UUID Cafe) {
        CafeEntity cafeEntity = cafeRepository.findByExternalId(Cafe)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro la cafeteria"));

        return productRepository.findByCafe(cafeEntity)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }


}