SELECT * FROM sbd.client;

SELECT count(*) FROM client WHERE last_name = "Almeida";

SET @param = '';
CALL count_last_name("Almeida", @param);
SELECT @param;

USE sbd;

SELECT name, price FROM Product JOIN Product_type ON Product.product_type_id = Product_type.id
WHERE Product_type.title = 'Main dish';

SELECT current_timestamp();

-- Ingredient in stock
SELECT
    ingredient.name, stock_ingredient.quantity
FROM
    ingredient
        JOIN
    stock_ingredient ON ingredient.id = stock_ingredient.ingredient_id
WHERE
    stock_ingredient.restaurant_id = 1
ORDER BY stock_ingredient.quantity DESC;

-- 3 most sold products in a week
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
    WEEK(order_processing.date) = WEEK('2017-01-29')
        AND client_order.restaurant_id = 1
GROUP BY client_order_product.product_id
ORDER BY SUM(client_order_product.product_quantity) DESC
LIMIT 3;

-- 3 most profitable products in a month
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
    MONTH(order_processing.date) = MONTH('2017-01-01')
        AND client_order.restaurant_id = 1
GROUP BY client_order_product.product_id
ORDER BY SUM(client_order_product.product_quantity) * product.price DESC
LIMIT 3;

-- Product's average preparation time
SELECT 
    t1.client_order_id,
    (t2.time - t1.time) / 100 AS 'Preperation time'
FROM
    order_processing AS t1
        LEFT JOIN
    order_processing AS t2 ON t1.client_order_id = t2.client_order_id
        JOIN
    client_order ON t1.client_order_id = client_order.id
WHERE
    t1.order_state_id = (SELECT order_state.id FROM order_state WHERE order_state.title = 'Accepted')
        AND t2.order_state_id = (SELECT order_state.id FROM order_state WHERE order_state.title = 'Ready')
        AND client_order.restaurant_id = 1
        AND t1.date = CURDATE()
ORDER BY client_order_id ASC;

-- Clients that spent more money in a trimester
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
    client_order.restaurant_id = 1
        AND FLOOR(ABS((MONTH(order_processing.date) - MONTH('2017-03-02')) / 3)) = 0
GROUP BY client.id
ORDER BY SUM(client_order_product.product_quantity * product.price) DESC;

-- Responsable employee for the preparation of an item
SELECT 
    order_processing.client_order_id AS 'Order #',
    order_state.title AS 'Product state',
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
    client_order.restaurant_id = 1
ORDER BY order_processing.client_order_id , order_processing.order_state_id ASC
