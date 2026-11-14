SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `categories` (`external_id`, `name`)
SELECT UUID_TO_BIN(UUID()), 'Otros' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `categories` WHERE `name` = 'Otros');

CREATE TABLE `catalog_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `name` varchar(80) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image` varchar(500) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `created_by_cafe_id` bigint DEFAULT NULL,
  `active` bit(1) NOT NULL DEFAULT b'1',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NULL,
  `version` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_catalog_products_external_id` (`external_id`),
  UNIQUE KEY `uk_catalog_products_name` (`name`),
  KEY `ix_catalog_products_category` (`category_id`),
  CONSTRAINT `fk_catalog_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `fk_catalog_products_created_by_cafe` FOREIGN KEY (`created_by_cafe_id`) REFERENCES `cafes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `catalog_products` (`external_id`, `name`, `description`, `image`, `category_id`, `created_by_cafe_id`)
SELECT UUID_TO_BIN(UUID()),
       `display_name`,
       `description`,
       `image`,
       COALESCE(`first_category_id`, (SELECT `id` FROM `categories` WHERE `name` = 'Otros')),
       `cafe_id`
FROM (
  SELECT CONCAT(UPPER(LEFT(TRIM(`name`), 1)), LOWER(SUBSTRING(TRIM(`name`), 2))) AS `display_name`,
         `description`,
         `image`,
         `cafe_id`,
         FIRST_VALUE(`category_id`) OVER (PARTITION BY TRIM(`name`) ORDER BY `category_id` IS NULL, `id`) AS `first_category_id`,
         ROW_NUMBER() OVER (PARTITION BY TRIM(`name`) ORDER BY `id`) AS `position`
  FROM `cafe_products`
) AS `grouped_products`
WHERE `position` = 1;

ALTER TABLE `cafe_products`
  ADD COLUMN `catalog_product_id` bigint NULL AFTER `cafe_id`,
  ADD COLUMN `custom_name` varchar(80) NULL AFTER `catalog_product_id`,
  ADD COLUMN `available` bit(1) NOT NULL DEFAULT b'1' AFTER `active`;

UPDATE `cafe_products` `offer`
JOIN `catalog_products` `catalog` ON `catalog`.`name` = TRIM(`offer`.`name`)
SET `offer`.`catalog_product_id` = `catalog`.`id`;

CREATE TEMPORARY TABLE `duplicated_offers` AS
SELECT `cafe_id`, `catalog_product_id`, MAX(`id`) AS `kept_id`
FROM `cafe_products`
WHERE `active` = b'1'
GROUP BY `cafe_id`, `catalog_product_id`
HAVING COUNT(*) > 1;

UPDATE `cafe_products` `offer`
JOIN `duplicated_offers` `duplicated`
  ON `duplicated`.`cafe_id` = `offer`.`cafe_id`
 AND `duplicated`.`catalog_product_id` = `offer`.`catalog_product_id`
SET `offer`.`active` = b'0'
WHERE `offer`.`id` <> `duplicated`.`kept_id`;

DROP TEMPORARY TABLE `duplicated_offers`;

UPDATE `cafe_products` SET `price` = 0 WHERE `price` IS NULL;
UPDATE `cafe_products` SET `awarded_points` = 0 WHERE `awarded_points` IS NULL;

ALTER TABLE `cafe_products`
  DROP FOREIGN KEY `FKog2rp4qthbtt2lfyhfo32lsw9`,
  DROP COLUMN `category_id`,
  DROP COLUMN `name`,
  MODIFY `cafe_id` bigint NOT NULL,
  MODIFY `catalog_product_id` bigint NOT NULL,
  MODIFY `price` decimal(12,2) NOT NULL,
  MODIFY `awarded_points` int NOT NULL DEFAULT 0,
  ADD COLUMN `active_catalog_product_id` bigint GENERATED ALWAYS AS (IF(`active` = b'1', `catalog_product_id`, NULL)) STORED,
  ADD CONSTRAINT `fk_cafe_products_catalog_product` FOREIGN KEY (`catalog_product_id`) REFERENCES `catalog_products` (`id`),
  ADD UNIQUE KEY `uk_cafe_products_active_offer` (`cafe_id`, `active_catalog_product_id`),
  ADD CONSTRAINT `ck_cafe_products_price` CHECK (`price` >= 0),
  ADD CONSTRAINT `ck_cafe_products_awarded_points` CHECK (`awarded_points` >= 0);

SET FOREIGN_KEY_CHECKS = 1;
