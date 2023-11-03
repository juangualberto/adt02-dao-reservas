-- borrado/creación de la BBDD en MySQL
-- sólo se hace una vez

DROP DATABASE reservas;
CREATE DATABASE reservas;

use reservas;

-- creación de las tablas
-- lo repetimos cada vez que se modifique la BBDD

DROP TABLE IF EXISTS `reserva`;
DROP TABLE IF EXISTS `horario`;
DROP TABLE IF EXISTS `instalacion`;
DROP TABLE IF EXISTS `usuario`;


CREATE TABLE `usuario`(
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(25), 
    `password` VARCHAR(64), 
    `email` VARCHAR(50), 
    `nombre` VARCHAR(40), 
    `apellido` VARCHAR(80), 
    `telefono` INT CONSTRAINT telefono_restric CHECK (`telefono` > 600000000 ), CHECK (`telefono`< 999999999));


CREATE TABLE `instalacion`(
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `nombre` VARCHAR(25), 
    `descripcion` VARCHAR(200));

CREATE TABLE `horario`(
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `hora_inicio` TIME, 
    `hora_fin` TIME, 
    `instalacion` INT NOT NULL,
    FOREIGN KEY (`instalacion`) REFERENCES `instalacion`(`id`));

CREATE TABLE `reserva`(
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `usuario` INT NOT NULL, 
    `horario` INT NOT NULL, 
    `fecha` DATETIME,
    FOREIGN KEY (`usuario`) REFERENCES `usuario`(`id`),
    FOREIGN KEY (`horario`) REFERENCES `horario`(`id`)
    );


INSERT INTO usuario (username, password, email, nombre, apellido, telefono) VALUES
('usuario1', 'clave1', 'usuario1@example.com', 'Nombre1', 'Apellido1', 612345678),
('usuario2', 'clave2', 'usuario2@example.com', 'Nombre2', 'Apellido2', 623456789),
('usuario3', 'clave3', 'usuario3@example.com', 'Nombre3', 'Apellido3', 634567890),
('usuario4', 'clave4', 'usuario4@example.com', 'Nombre4', 'Apellido4', 645678901),
('usuario5', 'clave5', 'usuario5@example.com', 'Nombre5', 'Apellido5', 656789012),
('usuario6', 'clave6', 'usuario6@example.com', 'Nombre6', 'Apellido6', 667890123),
('usuario7', 'clave7', 'usuario7@example.com', 'Nombre7', 'Apellido7', 678901234),
('usuario8', 'clave8', 'usuario8@example.com', 'Nombre8', 'Apellido8', 689012345),
('usuario9', 'clave9', 'usuario9@example.com', 'Nombre9', 'Apellido9', 690123456),
('usuario10', 'clave10', 'usuario10@example.com', 'Nombre10', 'Apellido10', 601234567);

INSERT INTO instalacion (nombre, descripcion) VALUES
('Instalacion1', 'Descripción de la Instalación 1'),
('Instalacion2', 'Descripción de la Instalación 2'),
('Instalacion3', 'Descripción de la Instalación 3'),
('Instalacion4', 'Descripción de la Instalación 4'),
('Instalacion5', 'Descripción de la Instalación 5'),
('Instalacion6', 'Descripción de la Instalación 6'),
('Instalacion7', 'Descripción de la Instalación 7'),
('Instalacion8', 'Descripción de la Instalación 8'),
('Instalacion9', 'Descripción de la Instalación 9'),
('Instalacion10', 'Descripción de la Instalación 10');

INSERT INTO horario (hora_inicio, hora_fin, instalacion) VALUES
('09:00:00', '10:00:00', 1),
('10:15:00', '11:15:00', 1),
('11:30:00', '12:30:00', 2),
('14:00:00', '15:00:00', 2),
('15:15:00', '16:15:00', 3),
('16:30:00', '17:30:00', 3),
('09:30:00', '10:30:00', 4),
('10:45:00', '11:45:00', 4),
('12:00:00', '13:00:00', 5),
('13:15:00', '14:15:00', 5);


INSERT INTO reserva (usuario, horario, fecha) VALUES
(1, 1, '2023-11-02 09:00:00'),
(2, 2, '2023-11-02 10:15:00'),
(3, 3, '2023-11-02 11:30:00'),
(4, 4, '2023-11-02 14:00:00'),
(5, 5, '2023-11-02 15:15:00'),
(6, 6, '2023-11-02 16:30:00'),
(7, 7, '2023-11-02 09:30:00'),
(8, 8, '2023-11-02 10:45:00'),
(9, 9, '2023-11-02 12:00:00'),
(10, 10, '2023-11-02 13:15:00');
