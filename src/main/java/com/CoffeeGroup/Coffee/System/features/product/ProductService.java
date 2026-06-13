package com.CoffeeGroup.Coffee.System.features.product;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final IProductRepository productRepository;
    private final IMapper<ProductEntity, ProductResponse> productMapper;
    private final CafeRepository cafeRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }


    @Override
    public ProductResponse getById(UUID productExternalId, UUID cafeExternalId) {

        CafeEntity cafe;

        if(securityUtils.isAdmin()){
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view the benefits of this coffee shop"));
        }


         ProductEntity entity = productRepository.findByExternalIdAndCafe(productExternalId,cafe)
                 .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return productMapper.toDto(entity);
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
    public List<ProductResponse> getAllByCafe(UUID cafeExternalId) {

        CafeEntity cafe;

        if(securityUtils.isAdmin()){
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view the benefits of this coffee shop"));
        }

        return productRepository.findByCafe(cafe).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductResponse> filterProductsByCafe(UUID cafeExternalId, String name, BigDecimal price, Integer awardedPoints) {

        CafeEntity cafe;
        if (securityUtils.isAdmin()) {
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view the products of this coffee shop"));
        }

        List<ProductEntity> filteredProducts = getFilteredProducts(cafe, name, price, awardedPoints);


        return filteredProducts.stream()
                .map(productMapper::toDto)
                .toList();
    }


    private List<ProductEntity> getFilteredProducts(CafeEntity cafe, String name, BigDecimal price, Integer awardedPoints) {


        if (name != null && price != null && awardedPoints != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndPriceAndAwardedPoints(cafe, name, price, awardedPoints);
        }

        if (name != null && price != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndPrice(cafe, name, price);
        }

        if (name != null && awardedPoints != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndAwardedPoints(cafe, name, awardedPoints);
        }

        if (price != null && awardedPoints != null) {
            return productRepository.findByCafeAndPriceAndAwardedPoints(cafe, price, awardedPoints);
        }

        if (name != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCase(cafe, name);
        }

        if (price != null) {
            return productRepository.findByCafeAndPrice(cafe, price);
        }

        if (awardedPoints != null) {
            return productRepository.findByCafeAndAwardedPoints(cafe, awardedPoints);
        }

        return productRepository.findByCafe(cafe);
    }












}