package com.CoffeeGroup.Coffee.System.features.media;

import org.springframework.http.HttpStatus;

import com.CoffeeGroup.Coffee.System.common.exceptions.DomainException;

public class MediaUploadNotConfiguredException extends DomainException {

    public MediaUploadNotConfiguredException() {
        super("Image upload is not configured");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public String title() {
        return "Image upload unavailable";
    }
}
