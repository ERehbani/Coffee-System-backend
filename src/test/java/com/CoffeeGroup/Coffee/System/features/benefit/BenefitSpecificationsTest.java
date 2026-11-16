package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class BenefitSpecificationsTest {

    @Test
    void withoutCafeTheCatalogIsNotFilteredByCafe() {
        assertThat(BenefitSpecifications.usableAt(null)).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void aCafeCanUseItsOwnBenefitsAndTheGlobalOnes() {
        UUID cafeId = UUID.randomUUID();
        Root<BenefitEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        Join<BenefitEntity, CafeEntity> cafe = mock(Join.class);
        Path<Object> externalId = mock(Path.class);
        Predicate isGlobal = mock(Predicate.class);
        Predicate isOwn = mock(Predicate.class);
        Predicate either = mock(Predicate.class);
        when(root.<BenefitEntity, CafeEntity>join("cafe", JoinType.LEFT)).thenReturn(cafe);
        when(cafe.get("externalId")).thenReturn(externalId);
        when(builder.isNull(cafe)).thenReturn(isGlobal);
        when(builder.equal(externalId, cafeId)).thenReturn(isOwn);
        when(builder.or(any(Predicate.class), any(Predicate.class))).thenReturn(either);

        Predicate predicate = BenefitSpecifications.usableAt(cafeId).toPredicate(root, query, builder);

        assertThat(predicate).isSameAs(either);
        verify(builder).or(isGlobal, isOwn);
    }
}
