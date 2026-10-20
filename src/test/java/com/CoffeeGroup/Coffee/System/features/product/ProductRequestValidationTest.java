package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ProductRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static ProductRequest request(String name, String description, BigDecimal price, Integer awardedPoints,
            UUID categoryId, String image) {
        return new ProductRequest(name, description, price, awardedPoints, categoryId, image);
    }

    private static ProductRequest validRequest() {
        return request("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), 100, UUID.randomUUID(),
                "https://cdn.example.com/cappuccino.png");
    }

    private static Set<String> invalidProperties(ProductRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(validRequest())).isEmpty();
    }

    @Test
    void missingCategoryIdIsInvalid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), 100, null, null);

        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        ConstraintViolation<ProductRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath()).hasToString("categoryId");
        assertThat(violation.getMessage()).isEqualTo("The category is required");
    }

    @Test
    void nullDescriptionIsValid() {
        ProductRequest request = request("Cappuccino", null, new BigDecimal("1500"), 100, UUID.randomUUID(), null);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void nullImageIsValid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), 100,
                UUID.randomUUID(), null);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void imageLongerThan500CharactersIsInvalid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), 100,
                UUID.randomUUID(), "x".repeat(501));

        assertThat(invalidProperties(request)).containsExactly("image");
    }

    @Test
    void priceZeroIsInvalid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", BigDecimal.ZERO, 100, UUID.randomUUID(),
                null);

        assertThat(invalidProperties(request)).containsExactly("price");
    }

    @Test
    void nullPriceIsInvalid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", null, 100, UUID.randomUUID(), null);

        assertThat(invalidProperties(request)).containsExactly("price");
    }

    @Test
    void nullAwardedPointsIsInvalid() {
        ProductRequest request = request("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), null,
                UUID.randomUUID(), null);

        assertThat(invalidProperties(request)).containsExactly("awardedPoints");
    }
}
