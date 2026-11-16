package com.CoffeeGroup.Coffee.System.common.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

public final class Specifications {

    private Specifications() {
    }

    @SafeVarargs
    public static <T> Specification<T> allOf(Specification<T>... specifications) {
        List<Specification<T>> present = Arrays.stream(specifications).filter(Objects::nonNull).toList();
        return Specification.allOf(present);
    }
}
