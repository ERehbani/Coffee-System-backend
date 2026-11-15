package com.CoffeeGroup.Coffee.System.features.media;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadSigner implements IMediaService {
    private static final String ALLOWED_FORMATS = "jpg,jpeg,png,webp,gif";

    private final CloudinaryProperties properties;
    private final Clock clock;

    @Override
    public UploadSignatureResponse signImageUpload() {
        if (!properties.isConfigured()) {
            throw new MediaUploadNotConfiguredException();
        }
        long timestamp = clock.instant().getEpochSecond();
        // Cloudinary signs the params sorted by name; these are already in alphabetical order.
        String params = "allowed_formats=" + ALLOWED_FORMATS + "&folder=" + properties.folder() + "&timestamp=" + timestamp;
        return new UploadSignatureResponse(properties.cloudName(), properties.apiKey(), timestamp,
                properties.folder(), ALLOWED_FORMATS, sha1Hex(params + properties.apiSecret()));
    }

    private static String sha1Hex(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is not available", e);
        }
    }
}
