SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `benefit_formats` (`external_id`, `code`, `display_name`, `description`, `active`)
SELECT UUID_TO_BIN(UUID()), `code`, `display_name`, `description`, b'1'
FROM (
  SELECT 'CODIGO_DESCUENTO' AS `code`, 'Descuento' AS `display_name`,
         'Reduce el precio final del pedido en un porcentaje.' AS `description`
  UNION ALL
  SELECT 'PRODUCTO_GRATIS_SOCIO', 'Producto gratis',
         'Entrega un producto del catálogo sin costo en puntos adicional.'
  UNION ALL
  SELECT 'DOS_POR_UNO', '2x1',
         'Entrega dos unidades del mismo producto al costo de una.'
  UNION ALL
  SELECT 'PRODUCTO_POR_PUNTOS', 'Producto por puntos',
         'Cambia puntos por un producto de la cafetería.'
) AS `formats`
WHERE NOT EXISTS (SELECT 1 FROM `benefit_formats` `existing` WHERE `existing`.`code` = `formats`.`code`);

ALTER TABLE `benefits`
  ADD COLUMN `format_id` bigint NULL AFTER `tier_id`,
  ADD COLUMN `discount_percentage` decimal(5,2) NULL AFTER `format_id`,
  ADD COLUMN `cafe_product_id` bigint NULL AFTER `discount_percentage`,
  ADD COLUMN `catalog_product_id` bigint NULL AFTER `cafe_product_id`,
  ADD COLUMN `redeemed_count` int NOT NULL DEFAULT 0 AFTER `max_redemptions`,
  ADD COLUMN `max_per_client` int NULL AFTER `redeemed_count`;

UPDATE `benefits` `benefit`
JOIN `types` `type` ON `type`.`id_type` = `benefit`.`type_id`
JOIN `benefit_formats` `format` ON `format`.`code` = `type`.`name`
SET `benefit`.`format_id` = `format`.`id`,
    `benefit`.`discount_percentage` = `type`.`porcentaje_descuento`,
    `benefit`.`cafe_product_id` = `type`.`producto_id`;

UPDATE `benefits` `benefit`
JOIN `cafe_products` `offer` ON `offer`.`id` = `benefit`.`cafe_product_id`
SET `benefit`.`catalog_product_id` = `offer`.`catalog_product_id`,
    `benefit`.`cafe_product_id` = NULL
WHERE `benefit`.`cafe_id` IS NULL;

UPDATE `benefits`
SET `format_id` = (SELECT `id` FROM `benefit_formats` WHERE `code` = 'CODIGO_DESCUENTO')
WHERE `format_id` IS NULL;

UPDATE `benefits` `benefit`
SET `benefit`.`redeemed_count` = (
  SELECT COUNT(*) FROM `redemptions` `redemption`
  WHERE `redemption`.`benefit_id` = `benefit`.`id`
    AND (`redemption`.`state` = b'1' OR `redemption`.`used_at` IS NOT NULL)
);

UPDATE `benefits`
SET `max_redemptions` = `max_redemptions` + `redeemed_count`
WHERE `max_redemptions` IS NOT NULL;

UPDATE `benefits` SET `name` = '' WHERE `name` IS NULL;
UPDATE `benefits` SET `active` = 1 WHERE `active` IS NULL;
UPDATE `benefits` SET `is_public` = b'0' WHERE `is_public` IS NULL;

ALTER TABLE `benefits`
  DROP FOREIGN KEY `FKs08avvgcnt1j54m0ad23fbj10`,
  DROP COLUMN `type_id`,
  DROP COLUMN `is_global`,
  MODIFY `name` varchar(255) NOT NULL,
  MODIFY `active` bit(1) NOT NULL DEFAULT b'1',
  MODIFY `is_public` bit(1) NOT NULL DEFAULT b'0',
  MODIFY `format_id` bigint NOT NULL,
  ADD CONSTRAINT `fk_benefits_format` FOREIGN KEY (`format_id`) REFERENCES `benefit_formats` (`id`),
  ADD CONSTRAINT `fk_benefits_cafe_product` FOREIGN KEY (`cafe_product_id`) REFERENCES `cafe_products` (`id`),
  ADD CONSTRAINT `fk_benefits_catalog_product` FOREIGN KEY (`catalog_product_id`) REFERENCES `catalog_products` (`id`),
  ADD CONSTRAINT `ck_benefits_points_cost` CHECK (`points_cost` >= 0),
  ADD CONSTRAINT `ck_benefits_max_redemptions` CHECK (`max_redemptions` IS NULL OR `max_redemptions` >= 0),
  ADD CONSTRAINT `ck_benefits_redeemed_count` CHECK (`redeemed_count` >= 0),
  ADD CONSTRAINT `ck_benefits_max_per_client` CHECK (`max_per_client` IS NULL OR `max_per_client` > 0),
  ADD CONSTRAINT `ck_benefits_discount_percentage` CHECK (`discount_percentage` IS NULL OR `discount_percentage` BETWEEN 0 AND 100),
  ADD INDEX `ix_benefits_cafe_available` (`cafe_id`, `is_public`, `active`, `points_cost`);

DROP TABLE `types`;

SET FOREIGN_KEY_CHECKS = 1;
