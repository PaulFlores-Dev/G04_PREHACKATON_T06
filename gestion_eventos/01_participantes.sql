USE gestion_eventos;

CREATE TABLE IF NOT EXISTS participantes (
    id_participante INT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni CHAR(8) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    institucion VARCHAR(150) NOT NULL,

    CONSTRAINT pk_participantes
            PRIMARY KEY (id_participante),

            CONSTRAINT uq_participantes_dni
                UNIQUE (dni),

            CONSTRAINT chk_participantes_dni
                CHECK (dni REGEXP '^[0-9]{8}$')
    ) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;