CREATE TABLE `cart_lines` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `cafe_product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_items_product` (`cart_id`, `cafe_product_id`),
  KEY `ix_cart_items_cafe_product` (`cafe_product_id`),
  CONSTRAINT `fk_cart_items_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`),
  CONSTRAINT `fk_cart_items_cafe_product` FOREIGN KEY (`cafe_product_id`) REFERENCES `cafe_products` (`id`),
  CONSTRAINT `ck_cart_items_quantity` CHECK (`quantity` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `cart_lines` (`cart_id`, `cafe_product_id`, `quantity`)
SELECT `item`.`cart_id`, `offer`.`id`, SUM(`item`.`quantity`)
FROM `cart_items` `item`
JOIN `cafe_products` `offer` ON `offer`.`external_id` = `item`.`product_id`
WHERE `item`.`quantity` > 0
GROUP BY `item`.`cart_id`, `offer`.`id`;

DROP TABLE `cart_items`;

RENAME TABLE `cart_lines` TO `cart_items`;

ALTER TABLE `carts` DROP COLUMN `total_price`;
