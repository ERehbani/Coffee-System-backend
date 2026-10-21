package com.CoffeeGroup.Coffee.System.common.exceptions;

/**
 * The payment provider could not be reached, rejected our request, or is not configured.
 */
public class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message) {
        super(message);
    }

    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
