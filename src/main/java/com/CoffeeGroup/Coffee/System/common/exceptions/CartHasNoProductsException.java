package com.CoffeeGroup.Coffee.System.common.exceptions;

import java.time.Instant;

public class CartHasNoProductsException extends RuntimeException {

    private final Instant timestamp;

    public CartHasNoProductsException(String message) {
        super(message);
        this.timestamp = Instant.now();
    }
}
