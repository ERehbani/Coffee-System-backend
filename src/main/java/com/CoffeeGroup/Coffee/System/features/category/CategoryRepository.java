package com.CoffeeGroup.Coffee.System.features.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByExternalId(UUID externalId);

    Optional<CategoryEntity> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndExternalIdNot(String name, UUID externalId);

    // Los productos del catálogo tienen la clave foránea que impide borrar una categoría en uso
    @Query("select p.category.id as categoryId, count(p) as productCount "
            + "from CatalogProductEntity p group by p.category.id")
    List<CategoryProductCount> countProductsByCategory();

    @Query("select count(p) from CatalogProductEntity p where p.category = :category")
    long countProducts(@Param("category") CategoryEntity category);

    interface CategoryProductCount {
        Long getCategoryId();

        Long getProductCount();
    }
}
