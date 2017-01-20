USE SBD;

CREATE PROCEDURE `get_owner` (IN in_restaurant_id int)
BEGIN
	SELECT id, full_name, email, mobile_number FROM employee WHERE restaurant_id = in_restaurant_id AND owner = true;
END

CREATE PROCEDURE `get_ready_orders`(IN in_date date, IN in_order_state varchar(20))
BEGIN
	SELECT client_order_id, MAX(order_state_id), employee_id, date FROM order_processing
	WHERE date = in_date
	GROUP BY client_order_id
	HAVING MAX(order_state_id) = (SELECT id FROM order_state WHERE title = in_order_state);
END
