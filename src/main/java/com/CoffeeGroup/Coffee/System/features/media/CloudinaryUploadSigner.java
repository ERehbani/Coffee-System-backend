package com.CoffeeGroup.Coffee.System.features.media;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadSigner implements IMediaService {
    private static final String ALLOWED_FORMATS = "jpg,jpeg,png,webp,gif";

    private final CloudinaryProperties properties;
    private final Clock clock;
    private final SecurityUtils securityUtils;

    @Override
    public UploadSignatureResponse signImageUpload(UploadTarget target) {
        UUID ownerId = ownerIdOf(target);
        if (!properties.isConfigured()) {
            throw new MediaUploadNotConfiguredException();
        }
        String folder = target.folderUnder(properties.folder(), ownerId);
        long timestamp = clock.instant().getEpochSecond();
        // Cloudinary signs the params sorted by name; these are already in alphabetical order.
        String params = "allowed_formats=" + ALLOWED_FORMATS + "&folder=" + folder + "&timestamp=" + timestamp;
        return new UploadSignatureResponse(properties.cloudName(), properties.apiKey(), timestamp,
                folder, ALLOWED_FORMATS, sha1Hex(params + properties.apiSecret()));
    }

    // The folder comes from the authenticated user so nobody can sign uploads into another owner's folder.
    private UUID ownerIdOf(UploadTarget target) {
        if (target.owner() == UploadTarget.Owner.CAFE && securityUtils.isCafe()) {
            return securityUtils.currentCafe().getExternalId();
        }
        if (target.owner() == UploadTarget.Owner.CLIENT && securityUtils.isClient()) {
            return securityUtils.currentClient().getExternalId();
        }
        throw new AccessDeniedException("You cannot upload a " + target + " image");
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
