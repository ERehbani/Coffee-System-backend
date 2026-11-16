CREATE TABLE `users` (
  `state` bit(1) NOT NULL,
  `creation_date` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcup9hom3h5cte4btcq935d0uu` (`external_id`),
  UNIQUE KEY `UK1ar956vx8jufbghpyi09yr16l` (`email_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tiers` (
  `min_points` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsbubxv5k1n05y6froq8gpphus` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK623ggp9v2uc3ufapf9smclaqu` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role` enum('ROLE_CAFETERIA','ROLE_CLIENTE','ROLE_SUPERADMIN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfatso2c7tbx8ak975h7n59dtb` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `permit_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permit` enum('ACTUALIZAR_PRODUCTOS','ACTUALIZAR_USUARIOS','CREAR_COMPRA','CREAR_PRODUCTOS','CREAR_USUARIOS','ELIMINAR_PRODUCTOS','ELIMINAR_USUARIOS','VER_COMPRAS_CAFETERIA','VER_COMPRAS_PROPIAS','VER_PRODUCTOS','VER_TODAS_LAS_COMPRAS','VER_USUARIOS') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKorxmyt96s8gpddhlo4a8tqm4l` (`permit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role_permits` (
  `permit_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`permit_id`,`role_id`),
  KEY `FKplm3q1nr83egju5jlffl8b6os` (`role_id`),
  CONSTRAINT `FKcey0gmsrlneqshtom8pupwbi8` FOREIGN KEY (`permit_id`) REFERENCES `permit_entity` (`id`),
  CONSTRAINT `FKplm3q1nr83egju5jlffl8b6os` FOREIGN KEY (`role_id`) REFERENCES `role_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `credentials_entity` (
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkmkdnbsbupcm375kaud4wrqdf` (`username`),
  UNIQUE KEY `UKk5y5ss3wh38kvoc8u2wq96jb8` (`usuario_id`),
  CONSTRAINT `FKe560gk37t3w97md8cpcp330po` FOREIGN KEY (`usuario_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `credentials_roles` (
  `credential_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`credential_id`,`role_id`),
  KEY `FKn5ogp7ge9di3vmlkhwaf1818x` (`role_id`),
  CONSTRAINT `FK4i67bfrub91k3rfphb3jfe538` FOREIGN KEY (`credential_id`) REFERENCES `credentials_entity` (`id`),
  CONSTRAINT `FKn5ogp7ge9di3vmlkhwaf1818x` FOREIGN KEY (`role_id`) REFERENCES `role_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cafes` (
  `state` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `external_id` binary(16) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `schedule` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4ocd1l9yon0xmoi1ecu1sj9bu` (`user_id`),
  UNIQUE KEY `UKhsdgela1qqi8x1kvs66cqwgwt` (`external_id`),
  UNIQUE KEY `UKghttkodt2wyn22aq9c2mfrhky` (`email_address`),
  CONSTRAINT `FK4jxkfavkvrmuik1r0h70c4wh4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `clients` (
  `current_points` int DEFAULT NULL,
  `historical_points` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tier_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `external_id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3xoivwlvijr7cgxph793y07hf` (`external_id`),
  UNIQUE KEY `UKsmrp6gi0tckq1w5rnd7boyowu` (`user_id`),
  KEY `FK5v2htdacjnjlmneo1pfkrq5dh` (`tier_id`),
  CONSTRAINT `FK5v2htdacjnjlmneo1pfkrq5dh` FOREIGN KEY (`tier_id`) REFERENCES `tiers` (`id`),
  CONSTRAINT `FKtiuqdledq2lybrds2k3rfqrv4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `products` (
  `awarded_points` int DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `state` bit(1) NOT NULL,
  `cafe_id` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `image` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg14e0ck7ng1yitxtkya2v9ded` (`external_id`),
  KEY `FK6t4jgvk0m807vdmqnt8sgdvrx` (`cafe_id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FK6t4jgvk0m807vdmqnt8sgdvrx` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_tags` (
  `product_id` bigint NOT NULL,
  `tag` varchar(30) DEFAULT NULL,
  KEY `FK5rk6s19k3risy7q7wqdr41uss` (`product_id`),
  CONSTRAINT `FK5rk6s19k3risy7q7wqdr41uss` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `types` (
  `porcentaje_descuento` double DEFAULT NULL,
  `cafe_id` bigint DEFAULT NULL,
  `id_type` bigint NOT NULL AUTO_INCREMENT,
  `producto_id` bigint DEFAULT NULL,
  `external_id` binary(16) NOT NULL,
  `name` enum('CODIGO_DESCUENTO','DOS_POR_UNO','PRODUCTO_GRATIS_SOCIO','PRODUCTO_POR_PUNTOS') NOT NULL,
  PRIMARY KEY (`id_type`),
  UNIQUE KEY `UKhqmgqce1d4rr87o888g97iy3t` (`external_id`),
  KEY `FK2cbcasui3k4qfrcq85ptvxs5e` (`cafe_id`),
  KEY `FKsxenk5ml3myi8s8fftfmkicfb` (`producto_id`),
  CONSTRAINT `FK2cbcasui3k4qfrcq85ptvxs5e` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKsxenk5ml3myi8s8fftfmkicfb` FOREIGN KEY (`producto_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `benefit_formats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` enum('CODIGO_DESCUENTO','DOS_POR_UNO','PRODUCTO_GRATIS_SOCIO','PRODUCTO_POR_PUNTOS') NOT NULL,
  `description` varchar(200) NOT NULL,
  `display_name` varchar(40) NOT NULL,
  `external_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsp7qqhav8l5roievdcsdmec0i` (`code`),
  UNIQUE KEY `UKrq3iw1pydj8mqrfm55jd7g0la` (`external_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `benefits` (
  `end_date` date DEFAULT NULL,
  `is_public` bit(1) DEFAULT NULL,
  `max_redemptions` int DEFAULT NULL,
  `points_cost` int NOT NULL,
  `start_date` date DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  `cafe_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tier_id` bigint DEFAULT NULL,
  `type_id` bigint DEFAULT NULL,
  `external_id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `is_global` enum('CAFFETERIA_BENEFIT','GLOBAL_BENEFIT') DEFAULT NULL,
  `conditions` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9ytnvwv32v55x1sxvq9tqiqcc` (`external_id`),
  KEY `FKs08avvgcnt1j54m0ad23fbj10` (`type_id`),
  KEY `FKaq0q3kk8ali91yk2gkvtqbbjb` (`cafe_id`),
  KEY `FKpr8aosvegdtb1ti67dr6loyrx` (`tier_id`),
  CONSTRAINT `FKaq0q3kk8ali91yk2gkvtqbbjb` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKpr8aosvegdtb1ti67dr6loyrx` FOREIGN KEY (`tier_id`) REFERENCES `tiers` (`id`),
  CONSTRAINT `FKs08avvgcnt1j54m0ad23fbj10` FOREIGN KEY (`type_id`) REFERENCES `types` (`id_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `carts` (
  `total_price` decimal(38,2) DEFAULT NULL,
  `cafe_id` bigint NOT NULL,
  `client_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg54cp3vysp91ne9anbon3rdc9` (`external_id`),
  KEY `FKidrr017epe5vtkdhxf0x6sou7` (`cafe_id`),
  KEY `FKifii0wg46vi0dhrtvailay156` (`client_id`),
  CONSTRAINT `FKidrr017epe5vtkdhxf0x6sou7` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKifii0wg46vi0dhrtvailay156` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cart_items` (
  `quantity` int NOT NULL,
  `cart_id` bigint NOT NULL,
  `product_id` binary(16) NOT NULL,
  KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `purchases` (
  `price` decimal(38,2) NOT NULL,
  `cafe_id` bigint DEFAULT NULL,
  `cart_id` bigint DEFAULT NULL,
  `client_id` bigint DEFAULT NULL,
  `date` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `redemption_id` bigint DEFAULT NULL,
  `external_id` binary(16) NOT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELLED','REFUNDED') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt6pekgfwhjpodplbgo6aq6qak` (`external_id`),
  UNIQUE KEY `UKr6rl93frddwlk4nou6utl5o1e` (`redemption_id`),
  KEY `FK565q3cuyadv1oj9a616cw8ufv` (`cafe_id`),
  KEY `FKjqf6yxso9c2fay9itpx9c3n7l` (`client_id`),
  KEY `FKjx919otj2fppd1qtij4jmxoof` (`cart_id`),
  CONSTRAINT `FK565q3cuyadv1oj9a616cw8ufv` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKjqf6yxso9c2fay9itpx9c3n7l` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  CONSTRAINT `FKjx919otj2fppd1qtij4jmxoof` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `purchase_details` (
  `amount` int NOT NULL,
  `generated_points` int NOT NULL,
  `unit_price` decimal(38,2) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `purchase_id` bigint NOT NULL,
  `external_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1jbb8p2ybwpmaw0u7801qbn0o` (`external_id`),
  KEY `FKcxl0u6f0whli7bhn1q8ja0ba3` (`product_id`),
  KEY `FK8nalokyn4ap9ebeo5hjjximls` (`purchase_id`),
  CONSTRAINT `FK8nalokyn4ap9ebeo5hjjximls` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`id`),
  CONSTRAINT `FKcxl0u6f0whli7bhn1q8ja0ba3` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `redemptions` (
  `state` bit(1) DEFAULT NULL,
  `used_points` int DEFAULT NULL,
  `benefit_id` bigint DEFAULT NULL,
  `cafe_id` bigint DEFAULT NULL,
  `client_id` bigint DEFAULT NULL,
  `date_redemption` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint DEFAULT NULL,
  `external_id` binary(16) NOT NULL,
  `validation_code` varchar(255) DEFAULT NULL,
  `expiration_date` datetime(6) DEFAULT NULL,
  `used_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnw35dtbnghp22mem2c7vssu01` (`external_id`),
  UNIQUE KEY `UK7u4rs1sb0xj3uxaq2djl03juu` (`purchase_id`),
  KEY `FK7atx2hr196it1pv0nayr9t1x` (`benefit_id`),
  KEY `FKcgse6i7v8wgir5qe59uhaipf3` (`cafe_id`),
  KEY `FKo5gsd2c3tugrm1c5a2v7j2dkn` (`client_id`),
  CONSTRAINT `FK7atx2hr196it1pv0nayr9t1x` FOREIGN KEY (`benefit_id`) REFERENCES `benefits` (`id`),
  CONSTRAINT `FKcgse6i7v8wgir5qe59uhaipf3` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKo5gsd2c3tugrm1c5a2v7j2dkn` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  CONSTRAINT `FKsl1dq6avccel9shu4x9kccjl0` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `purchases`
  ADD CONSTRAINT `FKl7jk3l8yvytus7wu9nj2xftl6` FOREIGN KEY (`redemption_id`) REFERENCES `redemptions` (`id`);

CREATE TABLE `reviews` (
  `date` date DEFAULT NULL,
  `cafe_id` bigint DEFAULT NULL,
  `client_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `external_id` binary(16) NOT NULL,
  `message` varchar(255) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `purchase_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8kubyu2j6bx1fno2wcpntk1op` (`external_id`),
  UNIQUE KEY `UKjfd0ugmaq72ipi087g9f158hs` (`purchase_id`),
  KEY `FKc5sisx3jgwje71tuv4irast9u` (`cafe_id`),
  KEY `FKo2cmyvyjrvumg4b3de9dcvfxa` (`client_id`),
  CONSTRAINT `FKc5sisx3jgwje71tuv4irast9u` FOREIGN KEY (`cafe_id`) REFERENCES `cafes` (`id`),
  CONSTRAINT `FKo2cmyvyjrvumg4b3de9dcvfxa` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  CONSTRAINT `FKtlbpgfgpje3g0do3xdmry3y9j` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
