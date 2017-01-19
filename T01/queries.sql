SELECT * FROM sbd.client;

SELECT count(*) FROM client WHERE last_name = "Almeida";

SET @param = '';
CALL count_last_name("Almeida", @param);
SELECT @param;