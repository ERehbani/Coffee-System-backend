ALTER TABLE `redemptions`
  MODIFY `status` enum('ACTIVE','USED','CANCELLED','EXPIRED') NOT NULL;
