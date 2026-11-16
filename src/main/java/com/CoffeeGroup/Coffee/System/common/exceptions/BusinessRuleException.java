package com.CoffeeGroup.Coffee.System.common.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends DomainException {

    public BusinessRuleException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.UNPROCESSABLE_CONTENT;
    }

    @Override
    public String title() {
        return "Business rule violated";
    }
}
