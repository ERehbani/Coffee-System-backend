package com.CoffeeGroup.Coffee.System.common.exceptions;

import java.time.Instant;
import java.util.NoSuchElementException;

public class EntityNotFoundException extends NoSuchElementException {

    private final String entity;
    private final String field;
    private final Object value;
    private final Instant timestamp;

    public EntityNotFoundException(String message) {
        super(message);
        this.entity = null;
        this.field = null;
        this.value = null;
        this.timestamp = Instant.now();
    }

    public EntityNotFoundException(String entity, String message, String field, Object value) {
        super(message);
        this.entity = entity;
        this.field = field;
        this.value = value;
        this.timestamp = Instant.now();
    }
}
