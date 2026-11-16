package com.CoffeeGroup.Coffee.System.common.exceptions;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String title() {
        return "Resource not found";
    }
}
