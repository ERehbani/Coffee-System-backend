package com.CoffeeGroup.Coffee.System.features.tier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum ETiersEnum {
    ESPRESSO(0),
    AMERICANO(500),
    LATTE(1500),
    CAPPUCINO(3500),
    BLACK_GOLD(7000);

    private final int minimumPoints;

    public int getMinimumPoints() {
        return minimumPoints;
    }
}
