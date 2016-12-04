CREATE DATABASE IF NOT EXISTS sbd;

USE sbd;

CREATE TABLE `client` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `mobile_number` VARCHAR(255) NOT NULL,
  `tax_number` INTEGER NOT NULL,
  `birth_date` DATE
);

CREATE TABLE `client_state` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `state` VARCHAR(255) NOT NULL
);

CREATE TABLE `employee_type` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL
);

CREATE TABLE `ingredient` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL
);

CREATE TABLE `order_state` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL
);

CREATE TABLE `product_menu` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `weekend` BOOLEAN NOT NULL,
  `open_time` TIME NOT NULL,
  `close_time` TIME NOT NULL
);

CREATE TABLE `product_type` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL
);

CREATE TABLE `product` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `sku` INTEGER NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `brand` VARCHAR(255),
  `expiration_date` DATE NOT NULL,
  `price` DECIMAL(12, 2) NOT NULL,
  `product_type_id` INTEGER NOT NULL
);

CREATE INDEX `idx_product__product_type_id` ON `product` (`product_type_id`);

ALTER TABLE `product` ADD CONSTRAINT `fk_product__product_type_id` FOREIGN KEY (`product_type_id`) REFERENCES `product_type` (`id`);

CREATE TABLE `product_ingredient` (
  `product_id` INTEGER NOT NULL,
  `ingredient_id` INTEGER NOT NULL,
  `quantity` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`product_id`, `ingredient_id`)
);

CREATE INDEX `idx_product_ingredient__ingredient_id` ON `product_ingredient` (`ingredient_id`);

ALTER TABLE `product_ingredient` ADD CONSTRAINT `fk_product_ingredient__ingredient_id` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`);

ALTER TABLE `product_ingredient` ADD CONSTRAINT `fk_product_ingredient__product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

CREATE TABLE `product_menu_type` (
  `product_menu_id` INTEGER NOT NULL,
  `product_type_id` INTEGER NOT NULL,
  `surplus` DECIMAL(12, 2) NOT NULL,
  PRIMARY KEY (`product_menu_id`, `product_type_id`)
);

CREATE INDEX `idx_product_menu_type__product_type_id` ON `product_menu_type` (`product_type_id`);

ALTER TABLE `product_menu_type` ADD CONSTRAINT `fk_product_menu_type__product_menu_id` FOREIGN KEY (`product_menu_id`) REFERENCES `product_menu` (`id`);

ALTER TABLE `product_menu_type` ADD CONSTRAINT `fk_product_menu_type__product_type_id` FOREIGN KEY (`product_type_id`) REFERENCES `product_type` (`id`);

CREATE TABLE `product_recipe` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `product_id` INTEGER NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(255) NOT NULL
);

CREATE INDEX `idx_product_recipe__product_id` ON `product_recipe` (`product_id`);

ALTER TABLE `product_recipe` ADD CONSTRAINT `fk_product_recipe__product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

CREATE TABLE `restaurant` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `postcode` VARCHAR(255) NOT NULL,
  `city` VARCHAR(255) NOT NULL,
  `country` VARCHAR(255) NOT NULL,
  `coordinates` VARCHAR(255) NOT NULL,
  `denomination` VARCHAR(255) NOT NULL,
  `logo` VARCHAR(255) NOT NULL
);

CREATE TABLE `client_order` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `client_id` INTEGER NOT NULL,
  `restaurant_id` INTEGER NOT NULL
);

CREATE INDEX `idx_client_order__client_id` ON `client_order` (`client_id`);

CREATE INDEX `idx_client_order__restaurant_id` ON `client_order` (`restaurant_id`);

ALTER TABLE `client_order` ADD CONSTRAINT `fk_client_order__client_id` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`);

ALTER TABLE `client_order` ADD CONSTRAINT `fk_client_order__restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`);

CREATE TABLE `client_order_product` (
  `order_id` INTEGER NOT NULL,
  `product_id` INTEGER NOT NULL,
  `product_quantity` INTEGER NOT NULL,
  PRIMARY KEY (`order_id`, `product_id`)
);

CREATE INDEX `idx_client_order_product__product_id` ON `client_order_product` (`product_id`);

ALTER TABLE `client_order_product` ADD CONSTRAINT `fk_client_order_product__order_id` FOREIGN KEY (`order_id`) REFERENCES `client_order` (`id`);

ALTER TABLE `client_order_product` ADD CONSTRAINT `fk_client_order_product__product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

CREATE TABLE `employee` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `full_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `mobile_number` VARCHAR(255) NOT NULL,
  `tax_number` VARCHAR(255) NOT NULL,
  `birth_date` VARCHAR(255) NOT NULL,
  `employee_type_id` INTEGER NOT NULL,
  `restaurant_id` INTEGER NOT NULL
);

CREATE INDEX `idx_employee__employee_type_id` ON `employee` (`employee_type_id`);

CREATE INDEX `idx_employee__restaurant_id` ON `employee` (`restaurant_id`);

ALTER TABLE `employee` ADD CONSTRAINT `fk_employee__employee_type_id` FOREIGN KEY (`employee_type_id`) REFERENCES `employee_type` (`id`);

ALTER TABLE `employee` ADD CONSTRAINT `fk_employee__restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`);

CREATE TABLE `client_activity` (
  `client_id` INTEGER NOT NULL,
  `client_state_id` INTEGER NOT NULL,
  `employee_id` INTEGER,
  `date` DATE NOT NULL,
  `time` TIME NOT NULL,
  PRIMARY KEY (`client_id`, `client_state_id`)
);

CREATE INDEX `idx_client_activity__client_state_id` ON `client_activity` (`client_state_id`);

CREATE INDEX `idx_client_activity__employee_id` ON `client_activity` (`employee_id`);

ALTER TABLE `client_activity` ADD CONSTRAINT `fk_client_activity__client_id` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`);

ALTER TABLE `client_activity` ADD CONSTRAINT `fk_client_activity__client_state_id` FOREIGN KEY (`client_state_id`) REFERENCES `client_state` (`id`);

ALTER TABLE `client_activity` ADD CONSTRAINT `fk_client_activity__employee_id` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`);

CREATE TABLE `order_processing` (
  `client_order_id` INTEGER NOT NULL,
  `order_state_id` INTEGER NOT NULL,
  `employee_id` INTEGER NOT NULL,
  `date` DATE NOT NULL,
  `time` TIME NOT NULL,
  `notes` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`client_order_id`, `order_state_id`, `employee_id`)
);

CREATE INDEX `idx_order_processing__employee_id` ON `order_processing` (`employee_id`);

CREATE INDEX `idx_order_processing__order_state_id` ON `order_processing` (`order_state_id`);

ALTER TABLE `order_processing` ADD CONSTRAINT `fk_order_processing__client_order_id` FOREIGN KEY (`client_order_id`) REFERENCES `client_order` (`id`);

ALTER TABLE `order_processing` ADD CONSTRAINT `fk_order_processing__employee_id` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`);

ALTER TABLE `order_processing` ADD CONSTRAINT `fk_order_processing__order_state_id` FOREIGN KEY (`order_state_id`) REFERENCES `order_state` (`id`);

CREATE TABLE `stock_ingredient` (
  `restaurant_id` INTEGER NOT NULL,
  `ingredient_id` INTEGER NOT NULL,
  `quantity` INTEGER NOT NULL,
  PRIMARY KEY (`restaurant_id`, `ingredient_id`)
);

CREATE INDEX `idx_stock_ingredient__ingredient_id` ON `stock_ingredient` (`ingredient_id`);

ALTER TABLE `stock_ingredient` ADD CONSTRAINT `fk_stock_ingredient__ingredient_id` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`);

ALTER TABLE `stock_ingredient` ADD CONSTRAINT `fk_stock_ingredient__restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`);

CREATE TABLE `stock_product` (
  `restaurant_id` INTEGER NOT NULL,
  `product_id` INTEGER NOT NULL,
  `quantity` INTEGER NOT NULL,
  PRIMARY KEY (`restaurant_id`, `product_id`)
);

CREATE INDEX `idx_stock_product__product_id` ON `stock_product` (`product_id`);

ALTER TABLE `stock_product` ADD CONSTRAINT `fk_stock_product__product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `stock_product` ADD CONSTRAINT `fk_stock_product__restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`);

CREATE TABLE `supplier` (
  `id` INTEGER PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `tax_number` INTEGER NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL
);

CREATE TABLE `supplier_ingredient` (
  `supplier_id` INTEGER NOT NULL,
  `ingredient_id` INTEGER NOT NULL,
  `favorite` BOOLEAN NOT NULL,
  PRIMARY KEY (`supplier_id`, `ingredient_id`)
);

CREATE INDEX `idx_supplier_ingredient__ingredient_id` ON `supplier_ingredient` (`ingredient_id`);

ALTER TABLE `supplier_ingredient` ADD CONSTRAINT `fk_supplier_ingredient__ingredient_id` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredient` (`id`);

ALTER TABLE `supplier_ingredient` ADD CONSTRAINT `fk_supplier_ingredient__supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`);

CREATE TABLE `supplier_product` (
  `supplier_id` INTEGER NOT NULL,
  `product_id` INTEGER NOT NULL,
  `favorite` BOOLEAN NOT NULL,
  PRIMARY KEY (`supplier_id`, `product_id`)
);

CREATE INDEX `idx_supplier_product__product_id` ON `supplier_product` (`product_id`);

ALTER TABLE `supplier_product` ADD CONSTRAINT `fk_supplier_product__product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);

ALTER TABLE `supplier_product` ADD CONSTRAINT `fk_supplier_product__supplier_id` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
