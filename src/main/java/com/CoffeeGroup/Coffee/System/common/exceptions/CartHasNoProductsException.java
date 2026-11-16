package com.CoffeeGroup.Coffee.System.common.exceptions;

import org.springframework.http.HttpStatus;

public class CartHasNoProductsException extends DomainException {

    public CartHasNoProductsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String title() {
        return "Empty cart";
    }
}
