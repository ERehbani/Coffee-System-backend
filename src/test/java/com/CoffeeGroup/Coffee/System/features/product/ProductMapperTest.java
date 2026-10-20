package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.mapper.ProductMapper;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    private static ProductEntity.ProductEntityBuilder productBuilder(CafeEntity cafe) {
        return ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Cappuccino")
                .description("Hot drink 12oz")
                .price(new BigDecimal("1500"))
                .awardedPoints(100)
                .state(true)
                .cafe(cafe);
    }

    @Test
    void toDtoExposesCategoryImageAndState() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        CategoryEntity category = CategoryEntity.builder().externalId(UUID.randomUUID()).name("Drinks").build();
        ProductEntity entity = productBuilder(cafe)
                .category(category)
                .image("https://cdn.example.com/cappuccino.png")
                .state(false)
                .build();

        ProductResponse response = mapper.toDto(entity);

        assertThat(response.externalId()).isEqualTo(entity.getExternalId());
        assertThat(response.cafe()).isEqualTo(cafe.getExternalId());
        assertThat(response.categoryId()).isEqualTo(category.getExternalId());
        assertThat(response.category()).isEqualTo("Drinks");
        assertThat(response.image()).isEqualTo("https://cdn.example.com/cappuccino.png");
        assertThat(response.state()).isFalse();
    }

    @Test
    void toDtoReturnsNullCategoryFieldsWhenProductHasNoCategory() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        ProductEntity entity = productBuilder(cafe).build();

        ProductResponse response = mapper.toDto(entity);

        assertThat(response.categoryId()).isNull();
        assertThat(response.category()).isNull();
        assertThat(response.image()).isNull();
    }

    @Test
    void toDtoReturnsAnEmptyListWhenProductHasNoTags() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();

        assertThat(mapper.toDto(productBuilder(cafe).build()).tags()).isNotNull().isEmpty();
    }

    @Test
    void toDtoReturnsAnEmptyListWhenTagsAreNull() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        ProductEntity entity = productBuilder(cafe).build();
        entity.setTags(null);

        assertThat(mapper.toDto(entity).tags()).isNotNull().isEmpty();
    }

    @Test
    void toDtoCopiesTagsSoLaterChangesToTheEntityDoNotLeakIntoTheResponse() {
        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        ProductEntity entity = productBuilder(cafe).tags(new ArrayList<>(List.of("vegan", "cold"))).build();

        ProductResponse response = mapper.toDto(entity);
        entity.getTags().add("sugar-free");

        assertThat(response.tags()).containsExactly("vegan", "cold");
    }
}
