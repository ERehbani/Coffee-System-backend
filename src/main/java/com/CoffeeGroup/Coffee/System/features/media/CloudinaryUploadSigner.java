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
        // Cloudinary firma los parámetros ordenados por nombre; estos ya están en orden alfabético
        String params = "allowed_formats=" + ALLOWED_FORMATS + "&folder=" + folder + "&timestamp=" + timestamp;
        return new UploadSignatureResponse(properties.cloudName(), properties.apiKey(), timestamp,
                folder, ALLOWED_FORMATS, sha1Hex(params + properties.apiSecret()));
    }

    // La carpeta sale del usuario autenticado para que nadie firme subidas en la carpeta de otro dueño
    private UUID ownerIdOf(UploadTarget target) {
        if (target.owner() == UploadTarget.Owner.CAFE && securityUtils.isCafe()) {
            return securityUtils.currentCafe().getExternalId();
        }
        if (target.owner() == UploadTarget.Owner.CLIENT && securityUtils.isClient()) {
            return securityUtils.currentClient().getExternalId();
        }
        throw new AccessDeniedException("No puedes subir imágenes para el destino " + target);
    }

    private static String sha1Hex(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no está disponible", e);
        }
    }
}
