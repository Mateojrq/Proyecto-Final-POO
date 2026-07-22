
CREATE TABLE IF NOT EXISTS usuarios (
                                        id              SERIAL PRIMARY KEY,
                                        nombres         VARCHAR(100)  NOT NULL,
    apellidos       VARCHAR(100)  NOT NULL,
    correo          VARCHAR(150)  NOT NULL UNIQUE,
    contrasena      VARCHAR(256)  NOT NULL,
    rol             VARCHAR(20)   NOT NULL CHECK (rol IN ('ADMIN', 'PROFESOR', 'ESTUDIANTE')),
    especialidad    VARCHAR(100),
    curso           VARCHAR(50),
    fecha_registro  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- ---------------------------------------------------------------------
-- Tabla tareas: los deberes que sube un profesor (recurso principal)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tareas (
                                      id               SERIAL PRIMARY KEY,
                                      titulo           VARCHAR(150)  NOT NULL,
    descripcion      TEXT          NOT NULL,
    fecha_entrega    DATE          NOT NULL,
    id_profesor      INTEGER       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    archivo          BYTEA,
    nombre_archivo   VARCHAR(255),
    fecha_creacion   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- ---------------------------------------------------------------------
-- Tabla entregas: relaciona un estudiante con una tarea (tabla de relacion)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS entregas (
                                        id               SERIAL PRIMARY KEY,
                                        id_tarea         INTEGER       NOT NULL REFERENCES tareas(id) ON DELETE CASCADE,
    id_estudiante    INTEGER       NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    archivo          BYTEA,
    nombre_archivo   VARCHAR(255),
    fecha_entrega    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    calificacion     NUMERIC(4,2),
    comentario       TEXT,
    estado           VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'CALIFICADO')),
    UNIQUE (id_tarea, id_estudiante)                 -- un estudiante no puede duplicar entrega
    );
