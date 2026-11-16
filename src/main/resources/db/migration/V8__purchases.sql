SET FOREIGN_KEY_CHECKS = 0;

UPDATE `purchases` SET `status` = 'CONFIRMED' WHERE `status` = 'PENDING';

ALTER TABLE `purchases`
  DROP FOREIGN KEY `FKjx919otj2fppd1qtij4jmxoof`;

ALTER TABLE `purchases`
  DROP INDEX `FKjx919otj2fppd1qtij4jmxoof`,
  DROP COLUMN `cart_id`,
  MODIFY `status` enum('CONFIRMED','CANCELLED','REFUNDED') NOT NULL,
  MODIFY `price` decimal(12,2) NOT NULL,
  MODIFY `client_id` bigint NOT NULL,
  MODIFY `cafe_id` bigint NOT NULL,
  ADD CONSTRAINT `ck_purchases_price` CHECK (`price` >= 0),
  ADD INDEX `ix_purchases_client_date` (`client_id`, `date`),
  ADD INDEX `ix_purchases_cafe_status_date` (`cafe_id`, `status`, `date`);

ALTER TABLE `purchase_details`
  MODIFY `unit_price` decimal(12,2) NOT NULL,
  ADD CONSTRAINT `ck_purchase_details_amount` CHECK (`amount` > 0),
  ADD CONSTRAINT `ck_purchase_details_unit_price` CHECK (`unit_price` >= 0),
  ADD CONSTRAINT `ck_purchase_details_generated_points` CHECK (`generated_points` >= 0);

SET FOREIGN_KEY_CHECKS = 1;
