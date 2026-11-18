package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.PointsRangeResponse;

@Component
public class ProductPointsPolicy {

    static final int MIN_POINTS = 1;
    static final int MAX_POINTS = 3000;

    private final CafeProductRepository cafeProductRepository;
    private final BigDecimal pesosPerPoint;
    private final BigDecimal maxDeviation;

    public ProductPointsPolicy(CafeProductRepository cafeProductRepository,
            @Value("${app.points.pesos-per-point:25}") BigDecimal pesosPerPoint,
            @Value("${app.points.max-deviation-percent:20}") int maxDeviationPercent) {
        if (pesosPerPoint.signum() <= 0) {
            throw new IllegalArgumentException("app.points.pesos-per-point must be positive");
        }
        if (maxDeviationPercent < 0 || maxDeviationPercent > 100) {
            throw new IllegalArgumentException("app.points.max-deviation-percent must be between 0 and 100");
        }
        this.cafeProductRepository = cafeProductRepository;
        this.pesosPerPoint = pesosPerPoint;
        this.maxDeviation = BigDecimal.valueOf(maxDeviationPercent).movePointLeft(2);
    }

    public PointsRangeResponse rangeFor(CafeEntity cafe, CatalogProductEntity catalogProduct, BigDecimal price) {
        BigDecimal total = price;
        long cafes = 1;
        if (catalogProduct != null) {
            CafeProductRepository.PriceStats others = cafeProductRepository.priceStatsOfOtherCafes(catalogProduct, cafe);
            total = total.add(others.getTotal());
            cafes += others.getCafes();
        }
        BigDecimal referencePrice = total.divide(BigDecimal.valueOf(cafes), 2, RoundingMode.HALF_UP);
        int suggested = bounded(referencePrice.divide(pesosPerPoint, 0, RoundingMode.HALF_UP));
        int min = bounded(BigDecimal.valueOf(suggested).multiply(BigDecimal.ONE.subtract(maxDeviation)));
        int max = bounded(BigDecimal.valueOf(suggested).multiply(BigDecimal.ONE.add(maxDeviation)));
        return new PointsRangeResponse(suggested, min, max, referencePrice, cafes);
    }

    public int resolve(Integer requested, PointsRangeResponse range) {
        if (requested == null) {
            return range.suggested();
        }
        if (!range.contains(requested)) {
            throw new BusinessRuleException("Awarded points for this product must be between %d and %d"
                    .formatted(range.min(), range.max()));
        }
        return requested;
    }

    private static int bounded(BigDecimal points) {
        int rounded = points.setScale(0, RoundingMode.HALF_UP).intValue();
        return Math.max(MIN_POINTS, Math.min(MAX_POINTS, rounded));
    }
}
