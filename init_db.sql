DROP TABLE IF EXISTS `reserva`;
DROP TABLE IF EXISTS `alojamiento`;
DROP TABLE IF EXISTS `cliente`;


CREATE TABLE cliente (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(15) UNIQUE NOT NULL
);


CREATE TABLE alojamiento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(15) NOT NULL
);


CREATE TABLE reserva (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cliente INT,
    alojamiento INT,
    entrada DATE NOT NULL,
    salida DATE NOT NULL,
    pension VARCHAR(50) NOT NULL,
    FOREIGN KEY (cliente) REFERENCES cliente(id),
    FOREIGN KEY (alojamiento) REFERENCES alojamiento(id)
);


