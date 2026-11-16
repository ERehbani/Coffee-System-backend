RENAME TABLE
  `credentials_entity` TO `credentials`,
  `role_entity` TO `roles`,
  `permit_entity` TO `permissions`,
  `role_permits` TO `role_permissions`,
  `products` TO `cafe_products`,
  `product_tags` TO `cafe_product_tags`;

ALTER TABLE `credentials` RENAME COLUMN `usuario_id` TO `user_id`;
ALTER TABLE `role_permissions` RENAME COLUMN `permit_id` TO `permission_id`;
ALTER TABLE `cafe_product_tags` RENAME COLUMN `product_id` TO `cafe_product_id`;
ALTER TABLE `purchase_details` RENAME COLUMN `product_id` TO `cafe_product_id`;

ALTER TABLE `users`
  RENAME COLUMN `state` TO `active`,
  RENAME COLUMN `creation_date` TO `created_at`;

ALTER TABLE `cafes` RENAME COLUMN `state` TO `active`;
ALTER TABLE `cafe_products` RENAME COLUMN `state` TO `active`;
ALTER TABLE `benefits` RENAME COLUMN `status` TO `active`;

ALTER TABLE `redemptions`
  RENAME COLUMN `date_redemption` TO `redeemed_at`,
  RENAME COLUMN `used_date` TO `used_at`;
