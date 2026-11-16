package com.CoffeeGroup.Coffee.System.features.media;

import java.util.UUID;

public enum UploadTarget {
    CAFE_LOGO(Owner.CAFE, "logo"),
    CAFE_COVER(Owner.CAFE, "cover"),
    PRODUCT_IMAGE(Owner.CAFE, "products"),
    CLIENT_AVATAR(Owner.CLIENT, "avatar");

    public enum Owner {
        CAFE("cafes"),
        CLIENT("clients");

        private final String folder;

        Owner(String folder) {
            this.folder = folder;
        }
    }

    private final Owner owner;
    private final String folder;

    UploadTarget(Owner owner, String folder) {
        this.owner = owner;
        this.folder = folder;
    }

    public Owner owner() {
        return owner;
    }

    public String folderUnder(String base, UUID ownerId) {
        return base + "/" + owner.folder + "/" + ownerId + "/" + folder;
    }
}
