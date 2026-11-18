package com.CoffeeGroup.Coffee.System.features.media;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureRequest;
import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Subida firmada de imágenes desde el navegador directo a Cloudinary")
public class MediaController {
    private final IMediaService mediaService;

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA','ROLE_CLIENTE')")
    @PostMapping("/signature")
    @Operation(summary = "Firmar la subida de una imagen", description = """
            Devuelve los parámetros firmados que el navegador envía a Cloudinary junto con la imagen; la firma vale una hora.
            El destino define la carpeta del dueño autenticado: CAFE_LOGO, CAFE_COVER y PRODUCT_IMAGE \
            firman en {base}/cafes/{cafeId}/logo, /cover o /products y requieren el rol de cafetería; CLIENT_AVATAR firma en \
            {base}/clients/{clientId}/avatar y requiere el rol de cliente. Un destino ausente o desconocido responde 400, \
            un destino de otro rol responde 403.""")
    public ResponseEntity<UploadSignatureResponse> signImageUpload(@Valid @RequestBody UploadSignatureRequest request) {
        return ResponseEntity.ok(mediaService.signImageUpload(request.target()));
    }
}
