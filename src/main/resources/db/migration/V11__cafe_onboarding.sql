ALTER TABLE `cafes`
  ADD COLUMN `logo` varchar(500) DEFAULT NULL,
  MODIFY COLUMN `image` varchar(500) DEFAULT NULL,
  DROP COLUMN `schedule`;

CREATE TABLE `cafe_opening_hours` (
  `cafe_id` bigint NOT NULL,
  `day_of_week` varchar(10) NOT NULL,
  `opens_at` time NOT NULL,
  `closes_at` time NOT NULL,
  PRIMARY KEY (`cafe_id`, `day_of_week`),
  CONSTRAINT `fk_cafe_opening_hours_cafe` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_cafe_opening_hours_day`
    CHECK (`day_of_week` IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
  CONSTRAINT `chk_cafe_opening_hours_range` CHECK (`opens_at` < `closes_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
