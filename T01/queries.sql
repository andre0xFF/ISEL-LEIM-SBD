SELECT * FROM sbd.client;

SELECT count(*) FROM client WHERE last_name = "Almeida";

SET @param = '';
CALL count_last_name("Almeida", @param);
SELECT @param;

CALL get_orders_by_state('2017-01-30', 'Ready');
CALL get_order_details(1);


SELECT client_order_id, order_state.title, employee_id, date FROM order_processing
JOIN order_state ON MAX(order_processing.order_state_id) = order_state.id
WHERE date = in_date
GROUP BY client_order_id
HAVING MAX(order_state_id) = (SELECT id FROM order_state WHERE title = in_order_state);


SELECT name, price FROM Product JOIN Product_type ON Product.product_type_id = Product_type.id
WHERE Product_type.title = 'Main dish';

SELECT current_timestamp();

-- Get orders by current state
SELECT client_order_id, order_state.title, employee_id, date, time FROM order_processing
JOIN order_state ON order_processing.order_state_id = order_state.id

WHERE date = '2017-01-30'
GROUP BY client_order_id

HAVING MAX(order_processing.order_state_id) = (SELECT order_state.id FROM order_state WHERE order_state.title = 'Ready')
ORDER BY client_order_id ASC;


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

-- Product's average preparation timeTIME_TO_SEC(`login`)
SELECT name, TIME(AVG(order_processing.time) - MIN(order_processing.time)) AS 'Average time' FROM product
JOIN client_order_product ON product.id = client_order_product.product_id
JOIN order_processing ON client_order_product.client_order_id = order_processing.client_order_id
JOIN client_order ON client_order_product.client_order_id = client_order.id
WHERE product.cook = TRUE AND client_order.restaurant_id = 1
GROUP BY product.id;

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

-- Get order details
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
    order_processing.client_order_id = 1
ORDER BY order_processing.client_order_id , order_processing.order_state_id ASC;

-- Increment order state
SELECT @order_state := MAX(order_state_id) FROM order_processing WHERE client_order_id = 30;
SELECT @max_order_state := MAX(id) FROM order_state;


SELECT
	ingredient.name, stock_ingredient.quantity
FROM
	ingredient
		JOIN
	stock_ingredient ON ingredient.id = stock_ingredient.ingredient_id
WHERE
	stock_ingredient.restaurant_id = 1
ORDER BY stock_ingredient.quantity DESC;


    
