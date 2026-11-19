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
@Tag(name = "Media", description = "Signed uploads of images straight from the browser to Cloudinary")
public class MediaController {
    private final IMediaService mediaService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA','ROLE_CLIENTE')")
    @PostMapping("/signature")
    @Operation(summary = "Sign image upload", description = """
            Returns the signed parameters the browser sends with the image to Cloudinary; the signature is valid for one hour.
            The target picks the folder of the authenticated owner: CAFE_LOGO, CAFE_COVER and PRODUCT_IMAGE \
            sign into {base}/cafes/{cafeId}/logo, /cover or /products and need the cafe role; CLIENT_AVATAR signs into \
            {base}/clients/{clientId}/avatar and needs the client role; BENEFIT_IMAGE signs into \
            {base}/cafes/{cafeId}/benefits for the cafe role and into {base}/global/benefits for the superadmin role. \
            A missing or unknown target answers 400, a target of another role answers 403.""")
    public ResponseEntity<UploadSignatureResponse> signImageUpload(@Valid @RequestBody UploadSignatureRequest request) {
        return ResponseEntity.ok(mediaService.signImageUpload(request.target()));
    }
}
