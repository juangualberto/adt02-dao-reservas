-- EJEMPLO DE TABLAS


-- las tablas se borran en orden inverso a su creación
-- para evitar que alguna FOREIGN KEY nos bloquee el borrado
DROP TABLE incidencia;
DROP TABLE inventario;
DROP TABLE estancia;
DROP TABLE usuario;


CREATE table if NOT EXISTS usuario (
    id int PRIMARY KEY AUTO_INCREMENT,
    username varchar(12) UNIQUE NOT NULL,
    password varchar(64) NOT NULL,
    tipo varchar(13) NOT NULL,
    -- Si usamos Java ENUM => "OPERARIO", "PROFESOR", "ALUMNO", "ADMIN"
    -- Si usamoe Java const int => 0, 1, 2, 3
    email varchar(50) UNIQUE NOT NULL
    -- Ejemplo: pedro_francisco_martinez_jimenez@g.educaand.es
);

CREATE table IF NOT EXISTS estancia(
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre varchar(30) NOT NULL,
    -- "Aula 1.8", "aula de tecnología"
    descripcion varchar(200),
    planta varchar(20) NOT NULL
);


CREATE TABLE IF NOT EXISTS inventario(
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre varchar(30) NOT NULL,
    descripcion varchar(200),
    estancia INT,
    FOREIGN KEY (estancia) REFERENCES estancia(id)
);

CREATE TABLE IF NOT EXISTS incidencia(
    id INT PRIMARY KEY AUTO_INCREMENT,
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



INSERT INTO usuario (id, username, password, tipo, email) VALUES
(1, 'pedro123', 'contrasena123', 'OPERARIO', 'pedro@example.com'),
(2, 'ana456', 'clave456', 'PROFESOR', 'ana@example.com'),
(3, 'carlos789', 'seguridad789', 'ALUMNO', 'carlos@example.com'),
(4, 'admin01', 'admin123', 'ADMIN', 'admin@example.com'),
(5, 'laura23', 'laura123', 'ALUMNO', 'laura@example.com'),
(6, 'jose567', 'jose123', 'OPERARIO', 'jose@example.com'),
(7, 'maria89', 'maria123', 'ADMIN', 'maria@example.com'),
(8, 'sara45', 'sara123', 'PROFESOR', 'sara@example.com'),
(9, 'pablo12', 'pablo123', 'ALUMNO', 'pablo@example.com'),
(10, 'elena34', 'elena123', 'OPERARIO', 'elena@example.com');


INSERT INTO estancia (id, nombre, descripcion, planta) VALUES
(1, 'Aula 1.8', 'Aula de tecnología', 'Planta 1'),
(2, 'Aula 2.5', 'Aula de matemáticas', 'Planta 2'),
(3, 'Laboratorio', 'Laboratorio de ciencias', 'Planta 1'),
(4, 'Biblioteca', 'Biblioteca universitaria', 'Planta 3'),
(5, 'Gimnasio', 'Gimnasio para estudiantes', 'Planta 2'),
(6, 'Cafetería', 'Cafetería del campus', 'Planta Baja'),
(7, 'Sala de conferencias', 'Sala para conferencias', 'Planta 4'),
(8, 'Sala de música', 'Sala de ensayos de música', 'Planta 1'),
(9, 'Sala de reuniones', 'Sala de reuniones para personal', 'Planta 5'),
(10, 'Aula 3.2', 'Aula de idiomas', 'Planta 3');


INSERT INTO inventario (id, nombre, descripcion, estancia) VALUES
(1, 'Computadoras', 'Computadoras para aula 1.8', 1),
(2, 'Proyectores', 'Proyectores para las aulas', 2),
(3, 'Microscopios', 'Microscopios para el laboratorio', 3),
(4, 'Libros', 'Colección de libros en la biblioteca', 4),
(5, 'Equipos de entrenamiento', 'Equipos de gimnasio', 5),
(6, 'Máquinas expendedoras', 'Máquinas de comida y bebida', 6),
(7, 'Proyector adicional', 'Proyector de reserva', 2),
(8, 'Instrumentos musicales', 'Instrumentos para la sala de música', 8),
(9, 'Proyector de conferencias', 'Proyector para la sala de conferencias', 7),
(10, 'Equipos de audio', 'Equipos de sonido para conferencias', 7);


INSERT INTO incidencia (id, asunto, descripcion, usuario, operario, fecha_inicio, fecha_fin, estado, inventario) VALUES
(1, 'Proyector dañado', 'El proyector no funciona en el aula 2.5', 3, 1, '2023-10-01', '2023-10-05', 'CERRADA', 2),
(2, 'Problema con el ordenador', 'La computadora no arranca en el aula 1.8', 4, 1, '2023-10-02', '2023-10-04', 'CERRADA', 1),
(3, 'Necesidad de material', 'Faltan microscopios en el laboratorio', 2, NULL, '2023-10-03', NULL, 'ABIERTA', 3),
(4, 'Libro perdido', 'Necesito encontrar un libro en la biblioteca', 5, NULL, '2023-10-10', NULL, 'ABIERTA', 4),
(5, 'Máquina de refrescos averiada', 'La máquina no entrega refrescos', 6, 2, '2023-10-11', '2023-10-13', 'CERRADA', 6),
(6, 'Clase de gimnasia cancelada', 'La clase de gimnasia se cancela hoy', 3, NULL, '2023-10-12', NULL, 'ABIERTA', 5),
(7, 'Problema con los instrumentos', 'Instrumentos musicales desafinados', 8, 10, '2023-10-05', '2023-10-07', 'CERRADA', 8),
(8, 'Solicitud de proyector', 'Necesitamos un proyector para la sala de conferencias', 7, NULL, '2023-10-15', NULL, 'ABIERTA', 9),
(9, 'Equipo de sonido no funciona', 'Problema con los equipos de audio', 10, 6, '2023-10-07', '2023-10-09', 'CERRADA', 10),
(10, 'Reserva de aula', 'Solicito reserva del aula 3.2 para mañana', 9, 7, '2023-10-16', '2023-10-17', 'EN PROCESO', 1);

