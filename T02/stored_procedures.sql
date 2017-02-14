USE `sbd`;
DROP procedure IF EXISTS `get_owner`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_owner` (IN in_restaurant_id integer)
BEGIN
	SELECT id, full_name, email, mobile_number FROM employee WHERE restaurant_id = in_restaurant_id AND owner = true;
END$$

DELIMITER ;


DROP procedure IF EXISTS `get_orders_with_state`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `get_ready_orders` (IN in_date date, IN in_order_state varchar(15))
BEGIN
	SELECT client_order_id, MAX(order_state_id), employee_id, date FROM order_processing
	WHERE date = in_date
	GROUP BY client_order_id
	HAVING MAX(order_state_id) = (SELECT id FROM order_state WHERE title = in_order_state);
END$$

DELIMITER ;


DROP procedure IF EXISTS `order_next_state`;

DELIMITER $$
USE `sbd`$$
CREATE PROCEDURE `order_next_state` (
	IN in_client_order_id integer,
  IN employee_id integer
)
BEGIN
	SELECT @order_state := MAX(order_state_id) FROM order_processing WHERE client_order_id = in_client_order_id;
	SELECT @max_order_state := MAX(id) FROM order_state;

    IF @max_order_state > @order_state THEN
		INSERT INTO `order_processing`
			(`client_order_id`,`order_state_id`,`employee_id`,`date`,`time`)
		VALUES
			(in_client_order_id, @order_state + 1, employee_id, curdate(), curtime());
	END IF;
END$$

DELIMITER ;
