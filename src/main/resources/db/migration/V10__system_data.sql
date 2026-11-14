INSERT IGNORE INTO `roles` (`role`) VALUES
  ('ROLE_SUPERADMIN'),
  ('ROLE_CLIENTE'),
  ('ROLE_CAFETERIA');

INSERT IGNORE INTO `permissions` (`permit`) VALUES
  ('VER_PRODUCTOS'),
  ('CREAR_PRODUCTOS'),
  ('ACTUALIZAR_PRODUCTOS'),
  ('ELIMINAR_PRODUCTOS'),
  ('VER_USUARIOS'),
  ('CREAR_USUARIOS'),
  ('ACTUALIZAR_USUARIOS'),
  ('ELIMINAR_USUARIOS'),
  ('CREAR_COMPRA'),
  ('VER_COMPRAS_PROPIAS'),
  ('VER_COMPRAS_CAFETERIA'),
  ('VER_TODAS_LAS_COMPRAS');

INSERT IGNORE INTO `role_permissions` (`role_id`, `permission_id`)
SELECT `role`.`id`, `permission`.`id`
FROM `roles` `role`
JOIN `permissions` `permission`
WHERE `role`.`role` = 'ROLE_SUPERADMIN'
   OR (`role`.`role` = 'ROLE_CLIENTE'
       AND `permission`.`permit` IN ('VER_PRODUCTOS', 'CREAR_COMPRA', 'VER_COMPRAS_PROPIAS'))
   OR (`role`.`role` = 'ROLE_CAFETERIA'
       AND `permission`.`permit` IN ('VER_PRODUCTOS', 'CREAR_PRODUCTOS', 'ACTUALIZAR_PRODUCTOS', 'VER_COMPRAS_CAFETERIA'));

INSERT IGNORE INTO `tiers` (`external_id`, `name`, `min_points`) VALUES
  (UUID_TO_BIN(UUID()), 'BRONCE', 0),
  (UUID_TO_BIN(UUID()), 'PLATA', 1000),
  (UUID_TO_BIN(UUID()), 'ORO', 3000),
  (UUID_TO_BIN(UUID()), 'DIAMANTE', 7000);

INSERT IGNORE INTO `categories` (`external_id`, `name`) VALUES
  (UUID_TO_BIN(UUID()), 'Cafe'),
  (UUID_TO_BIN(UUID()), 'Tes e Infusiones'),
  (UUID_TO_BIN(UUID()), 'Pasteleria'),
  (UUID_TO_BIN(UUID()), 'Sandwiches'),
  (UUID_TO_BIN(UUID()), 'Bebidas Frias'),
  (UUID_TO_BIN(UUID()), 'Otros');
