SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `redemptions`
  ADD COLUMN `status` enum('ACTIVE','USED','CANCELLED') NULL AFTER `state`;

UPDATE `redemptions`
SET `status` = CASE
  WHEN `state` = b'1' THEN 'ACTIVE'
  WHEN `used_at` IS NOT NULL THEN 'USED'
  ELSE 'CANCELLED'
END;

UPDATE `redemptions` SET `validation_code` = UUID() WHERE `validation_code` IS NULL;
UPDATE `redemptions` SET `used_points` = 0 WHERE `used_points` IS NULL;
UPDATE `redemptions` SET `redeemed_at` = `created_at` WHERE `redeemed_at` IS NULL;

ALTER TABLE `redemptions`
  DROP FOREIGN KEY `FKsl1dq6avccel9shu4x9kccjl0`,
  DROP FOREIGN KEY `FKcgse6i7v8wgir5qe59uhaipf3`;

ALTER TABLE `redemptions`
  DROP INDEX `UK7u4rs1sb0xj3uxaq2djl03juu`,
  DROP INDEX `FKcgse6i7v8wgir5qe59uhaipf3`,
  DROP COLUMN `purchase_id`,
  DROP COLUMN `cafe_id`,
  DROP COLUMN `state`,
  MODIFY `status` enum('ACTIVE','USED','CANCELLED') NOT NULL,
  MODIFY `validation_code` varchar(36) NOT NULL,
  MODIFY `used_points` int NOT NULL DEFAULT 0,
  MODIFY `redeemed_at` datetime(6) NOT NULL,
  MODIFY `client_id` bigint NOT NULL,
  MODIFY `benefit_id` bigint NOT NULL,
  ADD UNIQUE KEY `uk_redemptions_validation_code` (`validation_code`),
  ADD CONSTRAINT `ck_redemptions_used_points` CHECK (`used_points` >= 0),
  ADD INDEX `ix_redemptions_client_date` (`client_id`, `redeemed_at`);

SET @drop_expiration = (
  SELECT IF(COUNT(*) > 0, 'ALTER TABLE `redemptions` DROP COLUMN `expiration_date`', 'SELECT 1')
  FROM `information_schema`.`columns`
  WHERE `table_schema` = DATABASE() AND `table_name` = 'redemptions' AND `column_name` = 'expiration_date'
);
PREPARE `drop_expiration_statement` FROM @drop_expiration;
EXECUTE `drop_expiration_statement`;
DEALLOCATE PREPARE `drop_expiration_statement`;

SET FOREIGN_KEY_CHECKS = 1;
