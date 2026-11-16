package com.CoffeeGroup.Coffee.System.features.media;

import com.CoffeeGroup.Coffee.System.features.media.domain.dto.UploadSignatureResponse;

public interface IMediaService {
    UploadSignatureResponse signImageUpload(UploadTarget target);
}
