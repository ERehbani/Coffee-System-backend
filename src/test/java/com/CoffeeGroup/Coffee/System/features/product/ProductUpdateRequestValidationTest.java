package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ProductUpdateRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static UpdateProductRequest request(String name, String description, BigDecimal price,
            Integer awardedPoints, UUID categoryId, String image) {
        return new UpdateProductRequest(name, description, price, awardedPoints, categoryId, image, null);
    }

    private static UpdateProductRequest requestWithTags(List<String> tags) {
        return new UpdateProductRequest(null, null, null, null, null, null, tags);
    }

    private static Set<String> invalidProperties(UpdateProductRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    void emptyBodyIsValid() {
        assertThat(validator.validate(request(null, null, null, null, null, null))).isEmpty();
    }

    @Test
    void fullyPopulatedRequestIsValid() {
        UpdateProductRequest request = request("Latte", "Milk coffee 12oz", new BigDecimal("1800"), 120,
                UUID.randomUUID(), "https://cdn.example.com/latte.png");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void priceZeroIsInvalid() {
        assertThat(invalidProperties(request(null, null, BigDecimal.ZERO, null, null, null)))
                .containsExactly("price");
    }

    @Test
    void priceAboveMaximumIsInvalid() {
        assertThat(invalidProperties(request(null, null, new BigDecimal("50001"), null, null, null)))
                .containsExactly("price");
    }

    @Test
    void awardedPointsBelowMinimumIsInvalid() {
        assertThat(invalidProperties(request(null, null, null, 10, null, null)))
                .containsExactly("awardedPoints");
    }

    @Test
    void awardedPointsAboveMaximumIsInvalid() {
        assertThat(invalidProperties(request(null, null, null, 3001, null, null)))
                .containsExactly("awardedPoints");
    }

    @Test
    void blankNameIsInvalid() {
        assertThat(invalidProperties(request("   ", null, null, null, null, null)))
                .containsExactly("name");
    }

    @Test
    void nameWithDigitsIsInvalid() {
        assertThat(invalidProperties(request("Latte2", null, null, null, null, null)))
                .containsExactly("name");
    }

    @Test
    void blankDescriptionIsInvalid() {
        assertThat(invalidProperties(request(null, "      ", null, null, null, null)))
                .containsExactly("description");
    }

    @Test
    void imageLongerThan500CharactersIsInvalid() {
        assertThat(invalidProperties(request(null, null, null, null, null, "x".repeat(501))))
                .containsExactly("image");
    }

    @Test
    void emptyTagListIsValid() {
        assertThat(validator.validate(requestWithTags(List.of()))).isEmpty();
    }

    @Test
    void moreThanTenTagsAreInvalid() {
        List<String> tags = IntStream.rangeClosed(1, 11).mapToObj(i -> "tag" + i).toList();

        assertThat(invalidProperties(requestWithTags(tags))).containsExactly("tags");
    }

    @Test
    void blankTagIsInvalid() {
        assertThat(invalidProperties(requestWithTags(List.of("vegan", "   "))))
                .singleElement().asString().startsWith("tags[1]");
    }

    @Test
    void tagLongerThan30CharactersIsInvalid() {
        assertThat(invalidProperties(requestWithTags(List.of("x".repeat(31)))))
                .singleElement().asString().startsWith("tags[0]");
    }
}
