ALTER TABLE `users`
  MODIFY `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `tiers`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `categories`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `cafes`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `clients`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `cafe_products`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `carts`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `purchases`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `benefits`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `benefit_formats`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `redemptions`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

ALTER TABLE `reviews`
  ADD COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  ADD COLUMN `updated_at` datetime(6) NULL,
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;

UPDATE `purchases` SET `created_at` = `date`;
UPDATE `redemptions` SET `created_at` = `redeemed_at` WHERE `redeemed_at` IS NOT NULL;
UPDATE `reviews` SET `created_at` = `date` WHERE `date` IS NOT NULL;
