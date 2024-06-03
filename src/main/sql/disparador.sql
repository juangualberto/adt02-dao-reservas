DELIMITER //
DROP TRIGGER IF EXISTS `reservas_solapadas`//

CREATE TRIGGER `reservas_solapadas` 
BEFORE INSERT ON `reserva` 
FOR EACH ROW
BEGIN
    IF (SELECT COUNT(*) 
      FROM `reserva` 
      WHERE `reserva`.`cliente` = NEW.`cliente` AND 
        (`reserva`.`entrada` < NEW.`salida` 
        AND `reserva`.`salida` > NEW.`salida`)) > 0 
    THEN
      SIGNAL sqlstate '45001' 
        SET message_text = 'La fecha de salida se solapa con otra reserva.'; 
    END IF;

    IF (SELECT COUNT(*) 
      FROM `reserva` 
      WHERE `reserva`.`cliente` = NEW.`cliente` AND 
        (`reserva`.`entrada` < NEW.`entrada` 
        AND `reserva`.`salida` > NEW.`entrada`)) > 0 
    THEN
      SIGNAL sqlstate '45002' 
        SET message_text = 'La fecha de entrada se solapa con otra reserva.'; 
    END IF;

END//
DELIMITER ;