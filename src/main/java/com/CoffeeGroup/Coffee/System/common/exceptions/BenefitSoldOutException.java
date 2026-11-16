package com.CoffeeGroup.Coffee.System.common.exceptions;

import org.springframework.http.HttpStatus;

public class BenefitSoldOutException extends DomainException {

    public BenefitSoldOutException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String title() {
        return "Benefit sold out";
    }
}
