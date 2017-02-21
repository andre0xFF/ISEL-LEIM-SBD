USE `sbd`;

-- Get restaurant owner
DROP procedure IF EXISTS `get_owner`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_owner` (IN in_restaurant_id integer)
BEGIN
	SELECT id, full_name, email, mobile_number FROM employee WHERE restaurant_id = in_restaurant_id AND owner = true;
END$$

DELIMITER ;

-- Get orders by current state
DROP procedure IF EXISTS `get_orders_by_state`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_orders_by_state` (IN in_date date, IN in_order_state varchar(15), IN in_restaurant_id INTEGER)
BEGIN
    SELECT client_order_id, order_state.title, employee_id, date, time FROM order_processing
	JOIN order_state ON order_processing.order_state_id = order_state.id
    JOIN client_order ON order_processing.client_order_id = client_order.id

	WHERE date = in_date AND client_order.restaurant_id = in_restaurant_id
	GROUP BY client_order_id

	HAVING MAX(order_processing.order_state_id) = (SELECT order_state.id FROM order_state WHERE order_state.title = in_order_state)
	ORDER BY client_order_id ASC;
END$$

DELIMITER ;

-- Update an order to the next order state
DROP procedure IF EXISTS `order_next_state`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `order_next_state` (
	IN in_client_order_id integer,
	IN in_employee_id integer
)
BEGIN
	SET @order_state := (SELECT MAX(order_state_id) FROM order_processing WHERE client_order_id = in_client_order_id);
	SET @max_order_state := (SELECT MAX(id) FROM order_state);
    SET @order_id := (SELECT client_order_id FROM order_processing WHERE client_order_id = in_client_order_id LIMIT 1);

    IF @order_id > 0 AND @max_order_state > @order_state THEN
		INSERT INTO `order_processing`
			(`client_order_id`,`order_state_id`,`employee_id`,`date`,`time`)
		VALUES
			(in_client_order_id, @order_state + 1, in_employee_id, curdate(), curtime());
	END IF;
    
	SELECT client_order_id FROM order_processing
	WHERE client_order_id = in_client_order_id AND order_state_id = @order_state + 1;
END$$

DELIMITER ;

-- Get order details
DROP procedure IF EXISTS `get_order_details`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_order_details` (
	IN in_client_order_id integer
)
BEGIN
	SELECT
		order_processing.client_order_id AS 'Order #',
		order_state.title AS 'Product state',
        order_processing.date,
		order_processing.time,
		employee.full_name AS 'Employee'
	FROM
		Employee
			JOIN
		order_processing ON employee.id = order_processing.employee_id
			JOIN
		order_state ON order_processing.order_state_id = order_state.id
			JOIN
		client_order ON order_processing.client_order_id = client_order.id
	WHERE
		order_processing.client_order_id = in_client_order_id
	ORDER BY order_processing.client_order_id , order_processing.order_state_id ASC;
END$$

DELIMITER ;

-- Clients revenue by trimester
USE `sbd`;
DROP procedure IF EXISTS `clients_revenue_by_trimester`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_clients_revenue_by_trimester` (
	IN in_date DATE,
	IN in_restaurant_id INTEGER
)
BEGIN
	SELECT 
		CONCAT(client.first_name, ' ', client.last_name) AS 'Client name',
		SUM(client_order_product.product_quantity) AS 'Quantity of products',
		SUM(client_order_product.product_quantity * product.price) AS 'Total amount spent'
	FROM
		client
			JOIN
		client_order ON client.id = client_order.id
			JOIN
		client_order_product ON client_order.id = client_order_product.client_order_id
			JOIN
		product ON client_order_product.product_id = product.id
			JOIN
		order_processing ON client_order.id = order_processing.client_order_id
	WHERE
		client_order.restaurant_id = in_restaurant_id
			AND FLOOR(ABS((MONTH(order_processing.date) - MONTH(in_date)) / 3)) = 0
	GROUP BY client.id
	ORDER BY SUM(client_order_product.product_quantity * product.price) DESC;
END$$

DELIMITER ;

-- Get product's average preparation time
USE `sbd`;
DROP procedure IF EXISTS `get_products_avg_time`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_products_avg_time` (
	IN in_restaurant_id INTEGER
)
BEGIN
	SELECT name, TIME(AVG(order_processing.time) - MIN(order_processing.time)) AS 'Average time' FROM product
	JOIN client_order_product ON product.id = client_order_product.product_id
	JOIN order_processing ON client_order_product.client_order_id = order_processing.client_order_id
	JOIN client_order ON client_order_product.client_order_id = client_order.id
	WHERE product.cook = TRUE AND client_order.restaurant_id = in_restaurant_id
	GROUP BY product.id;
END$$

DELIMITER ;

-- Get the 3 most profitable products in a month
USE `sbd`;
DROP procedure IF EXISTS `get_most_profitable_products_by_month`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_most_profitable_products_by_month` (
	IN in_date DATE,
    IN in_restaurant_id INTEGER
)
BEGIN
	SELECT
		product.name AS 'Product', SUM(client_order_product.product_quantity) 'Quantity of products',
		SUM(client_order_product.product_quantity) * product.price AS 'Revenue'
	FROM
		product
			JOIN
		client_order_product ON product.id = client_order_product.product_id
			JOIN
		client_order ON client_order_product.client_order_id = client_order.id
			JOIN
		order_processing ON client_order.id = order_processing.client_order_id
	WHERE
		MONTH(order_processing.date) = MONTH(in_date)
			AND client_order.restaurant_id = in_restaurant_id
	GROUP BY client_order_product.product_id
	ORDER BY SUM(client_order_product.product_quantity) * product.price DESC
	LIMIT 3;
END$$

DELIMITER ;

-- Get the 3 most sold products in a week
USE `sbd`;
DROP procedure IF EXISTS `get_most_sold_products_by_week`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_most_sold_products_by_week` (
	IN in_restaurant_id INTEGER,
    IN in_date DATE
)
BEGIN
	SELECT 
		product.name AS 'Product',
		SUM(client_order_product.product_quantity) AS 'Quantity of products'
	FROM
		product
			JOIN
		client_order_product ON product.id = client_order_product.product_id
			JOIN
		client_order ON client_order_product.client_order_id = client_order.id
			JOIN
		order_processing ON client_order.id = order_processing.client_order_id
	WHERE
		WEEK(order_processing.date) = WEEK(in_date)
			AND client_order.restaurant_id = in_restaurant_id
	GROUP BY client_order_product.product_id
	ORDER BY SUM(client_order_product.product_quantity) DESC
	LIMIT 3;
END$$

DELIMITER ;

-- Get ingredients stock
USE `sbd`;
DROP procedure IF EXISTS `get_ingredients_stock`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_ingredients_stock` (
	IN in_restaurant_id INTEGER
)
BEGIN
	SELECT
		ingredient.name, stock_ingredient.quantity
	FROM
		ingredient
			JOIN
		stock_ingredient ON ingredient.id = stock_ingredient.ingredient_id
	WHERE
		stock_ingredient.restaurant_id = in_restaurant_id
	ORDER BY stock_ingredient.quantity DESC;
END$$

DELIMITER ;

-- Get client's last order
USE `sbd`;
DROP procedure IF EXISTS `get_client_last_order`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_client_last_order` (
	IN in_client_id INTEGER,
    IN in_date DATE
)
BEGIN
	SELECT order_state.title, date, time, SUM(product_quantity * price) AS 'Order total'FROM order_processing
	JOIN order_state ON order_processing.order_state_id = order_state.id
	JOIN client_order ON order_processing.client_order_id = client_order.id
	JOIN client_order_product ON order_processing.client_order_id = client_order_product.client_order_id
	JOIN product ON client_order_product.product_id = product.id
	WHERE client_order.id = in_client_id AND date = in_date
	ORDER BY order_state.id DESC
	LIMIT 1;
END$$

DELIMITER ;

-- Get employee
USE `sbd`;
DROP procedure IF EXISTS `get_employee`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_employee` (IN in_email varchar(40))
BEGIN
	SELECT employee.id, employee_type.title, full_name, email, mobile_number, tax_number, birth_date FROM employee
    JOIN employee_type ON employee_type.id = employee.employee_type_id
    WHERE employee.email = in_email;
END$$

DELIMITER ;



