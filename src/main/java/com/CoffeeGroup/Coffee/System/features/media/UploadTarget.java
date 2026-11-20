package com.CoffeeGroup.Coffee.System.features.media;

import java.util.Set;
import java.util.UUID;

public enum UploadTarget {
    CAFE_LOGO("logo", Owner.CAFE),
    CAFE_COVER("cover", Owner.CAFE),
    PRODUCT_IMAGE("products", Owner.CAFE),
    BENEFIT_IMAGE("benefits", Owner.CAFE, Owner.PLATFORM),
    CLIENT_AVATAR("avatar", Owner.CLIENT);

    public enum Owner {
        CAFE("cafes"),
        CLIENT("clients"),
        // The platform (superadmin) owns shared content such as global benefits, so its folder has no owner id.
        PLATFORM("global");

        private final String folder;

        Owner(String folder) {
            this.folder = folder;
        }

        public String folder() {
            return folder;
        }

        public String folderOf(UUID ownerId) {
            return folder + "/" + ownerId;
        }
    }

    private final String folder;
    private final Set<Owner> owners;

    UploadTarget(String folder, Owner... owners) {
        this.folder = folder;
        this.owners = Set.of(owners);
    }

    public boolean isUploadableBy(Owner owner) {
        return owners.contains(owner);
    }

    public String folderUnder(String base, String ownerFolder) {
        return base + "/" + ownerFolder + "/" + folder;
    }
}
