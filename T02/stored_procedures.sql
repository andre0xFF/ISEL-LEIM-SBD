USE `sbd`;
DROP procedure IF EXISTS `get_owner`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_owner` ()
BEGIN
	SELECT id, full_name, email, mobile_number FROM employee WHERE restaurant_id = in_restaurant_id AND owner = true;
END$$

DELIMITER ;

DROP procedure IF EXISTS `get_ready_orders`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_ready_orders` ()
BEGIN
	SELECT client_order_id, MAX(order_state_id), employee_id, date FROM order_processing
	WHERE date = in_date
	GROUP BY client_order_id
	HAVING MAX(order_state_id) = (SELECT id FROM order_state WHERE title = in_order_state);
END$$

DELIMITER ;
