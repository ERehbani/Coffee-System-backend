package com.CoffeeGroup.Coffee.System.features.media;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Signed uploads of images straight from the browser to Cloudinary")
public class MediaController {
    private final IMediaService mediaService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/signature")
    @Operation(summary = "Sign image upload", description = "Returns the signed parameters the browser sends with the image to Cloudinary; the signature is valid for one hour")
    public ResponseEntity<UploadSignatureResponse> signImageUpload() {
        return ResponseEntity.ok(mediaService.signImageUpload());
    }
}
