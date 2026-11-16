package com.CoffeeGroup.Coffee.System.common.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientPointsException extends DomainException {

    public InsufficientPointsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.UNPROCESSABLE_CONTENT;
    }

    @Override
    public String title() {
        return "Insufficient points";
    }
}
