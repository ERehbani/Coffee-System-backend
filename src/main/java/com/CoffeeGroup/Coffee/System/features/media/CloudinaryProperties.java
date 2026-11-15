package com.CoffeeGroup.Coffee.System.features.media;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cloudinary")
public record CloudinaryProperties(String cloudName, String apiKey, String apiSecret, String folder) {

    public boolean isConfigured() {
        return hasText(cloudName) && hasText(apiKey) && hasText(apiSecret);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    // The generated record toString would print the API secret wherever this object is logged.
    @Override
    public String toString() {
        return "CloudinaryProperties[cloudName=" + cloudName + ", apiKey=" + apiKey + ", folder=" + folder + "]";
    }
}
