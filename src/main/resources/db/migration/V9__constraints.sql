SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `tiers`
  MODIFY `name` varchar(50) NOT NULL,
  MODIFY `min_points` int NOT NULL,
  ADD UNIQUE KEY `uk_tiers_name` (`name`),
  ADD UNIQUE KEY `uk_tiers_min_points` (`min_points`),
  ADD CONSTRAINT `ck_tiers_min_points` CHECK (`min_points` >= 0);

ALTER TABLE `categories`
  MODIFY `name` varchar(50) NOT NULL,
  ADD UNIQUE KEY `uk_categories_name` (`name`);

UPDATE `clients` SET `current_points` = 0 WHERE `current_points` IS NULL;
UPDATE `clients` SET `historical_points` = 0 WHERE `historical_points` IS NULL;

ALTER TABLE `clients`
  MODIFY `current_points` int NOT NULL DEFAULT 0,
  MODIFY `historical_points` int NOT NULL DEFAULT 0,
  MODIFY `user_id` bigint NOT NULL,
  ADD CONSTRAINT `ck_clients_current_points` CHECK (`current_points` >= 0),
  ADD CONSTRAINT `ck_clients_historical_points` CHECK (`historical_points` >= 0);

ALTER TABLE `credentials`
  MODIFY `user_id` bigint NOT NULL;

ALTER TABLE `cafes`
  DROP INDEX `UKghttkodt2wyn22aq9c2mfrhky`,
  DROP COLUMN `email_address`;

ALTER TABLE `carts`
  ADD UNIQUE KEY `uk_carts_client_cafe` (`client_id`, `cafe_id`);

ALTER TABLE `reviews`
  ADD CONSTRAINT `ck_reviews_rating` CHECK (`rating` IS NULL OR `rating` BETWEEN 1 AND 5);

ALTER TABLE `cafe_product_tags`
  MODIFY `tag` varchar(30) NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;
