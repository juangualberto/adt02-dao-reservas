-- EJEMPLO DE TABLAS


-- las tablas se borran en orden inverso a su creación
-- para evitar que alguna FOREIGN KEY nos bloquee el borrado
DROP TABLE incidencia;
DROP TABLE inventario;
DROP TABLE estancia;
DROP TABLE usuario;


CREATE table if NOT EXISTS usuario (
    id int PRIMARY KEY,
    username varchar(12) UNIQUE NOT NULL,
    password varchar(64) NOT NULL,
    tipo varchar(13) NOT NULL,
    -- Si usamos Java ENUM => "OPERARIO", "PROFESOR", "ALUMNO", "ADMIN"
    -- Si usamoe Java const int => 0, 1, 2, 3
    email varchar(50) UNIQUE NOT NULL
    -- Ejemplo: pedro_francisco_martinez_jimenez@g.educaand.es
);

CREATE table IF NOT EXISTS estancia(
    id INT PRIMARY KEY,
    nombre varchar(30) NOT NULL,
    -- "Aula 1.8", "aula de tecnología"
    descripcion varchar(200),
    planta varchar(10) NOT NULL
);


CREATE TABLE IF NOT EXISTS inventario(
    id INT PRIMARY KEY,
    nombre varchar(30) NOT NULL,
    descripcion varchar(200),
    estancia INT,
    FOREIGN KEY (estancia) REFERENCES estancia(id)
);

CREATE TABLE IF NOT EXISTS incidencia(
    id INT PRIMARY KEY,
    asunto varchar(80),
    descripcion varchar(200),
    usuario INT NOT NULL,
    operario INT,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado varchar(20),
    -- Java ENUM
    -- "ABIERTA", "CERRADA", "EN PROCESO", "ESPERANDO MATERIAL"
    inventario INT NOT NULL,
    FOREIGN KEY (usuario) REFERENCES usuario(id),
    FOREIGN KEY (operario) REFERENCES usuario(id),
    FOREIGN KEY (inventario) REFERENCES inventario(id)
);