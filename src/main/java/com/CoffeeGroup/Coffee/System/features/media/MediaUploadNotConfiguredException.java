package com.CoffeeGroup.Coffee.System.features.media;

import org.springframework.http.HttpStatus;

import com.CoffeeGroup.Coffee.System.common.exceptions.DomainException;

public class MediaUploadNotConfiguredException extends DomainException {

    public MediaUploadNotConfiguredException() {
        super("La subida de imágenes no está configurada");
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public String title() {
        return "Subida de imágenes no disponible";
    }
}
