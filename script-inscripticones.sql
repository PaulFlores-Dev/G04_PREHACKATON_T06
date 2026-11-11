USE BD_T04;

CREATE TABLE IF NOT EXISTS inscripcionesid_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_participante INT NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    fecha_inscripcion VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'Confirmado'
    );