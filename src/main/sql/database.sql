DROP DATABASE `inventario`;

CREATE DATABASE `inventario`;

-- usuario

CREATE TABLE `usuario` (   
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,   
    `username` varchar(12) UNIQUE NOT NULL,   
    `password` varchar(20) NOT NULL,   
    `tipo` ENUM('admin', 'usuario', 'operario'),
    `email` varchar(50) NOT NULL ) ENGINE='InnoDB';

-- estancia

CREATE TABLE `estancia` (
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,   
    `nombre` varchar(25) UNIQUE NOT NULL,   
    `descripcion` varchar(100) NOT NULL
) ENGINE='InnoDB';

-- inventario

CREATE TABLE `inventario` (
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,   
    `nombre` varchar(25) UNIQUE NOT NULL,   
    `descripcion` varchar(100) NOT NULL, 
    `estancia` INT, 
    Foreign Key (`estancia`) REFERENCES `estancia`(`id`)
) ENGINE='InnoDB';

-- incidencias

CREATE TABLE `incidencia` (
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,   
    `asunto` varchar(25) UNIQUE NOT NULL,   
    `descripcion` varchar(100) NOT NULL, 
    `inventario` int, 
    `operario` int, 
    `usuario` int,
    `estado` ENUM('abierta', 'en proceso', 'cerrada', 'pendiente externo'), 
    Foreign Key (inventario) REFERENCES inventario(id),
    Foreign Key (operario) REFERENCES usuario(id),
    Foreign Key (usuario) REFERENCES usuario(id)
) ENGINE='InnoDB';

