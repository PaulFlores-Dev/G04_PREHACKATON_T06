CREATE DATABASE IF NOT EXISTS BD_T04;
USE BD_T04;

CREATE TABLE IF NOT EXISTS eventos (
                         id_evento INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion TEXT,
                         fecha DATE NOT NULL,
                         lugar VARCHAR(150) NOT NULL,
                         modalidad VARCHAR(50) NOT NULL,
                         estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS participantes (
                               id_participante INT AUTO_INCREMENT PRIMARY KEY,
                               nombres VARCHAR(100) NOT NULL,
                               apellidos VARCHAR(100) NOT NULL,
                               dni VARCHAR(15) UNIQUE NOT NULL,
                               correo VARCHAR(100) NOT NULL,
                               telefono VARCHAR(20),
                               institucion VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS inscripciones (
                               id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
                               id_evento INT NOT NULL,
                               id_participante INT NOT NULL,
                               fecha_inscripcion DATETIME DEFAULT CURRENT_TIMESTAMP,
                               estado VARCHAR(50) NOT NULL,
                               FOREIGN KEY (id_evento) REFERENCES eventos(id_evento) ON DELETE CASCADE,
                               FOREIGN KEY (id_participante) REFERENCES participantes(id_participante) ON DELETE CASCADE
);