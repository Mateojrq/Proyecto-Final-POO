CREATE DATABASE db_centro_nivelacion;


CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(100) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          rol VARCHAR(20) NOT NULL,
                          especialidad VARCHAR(100)
);

CREATE TABLE tareas (
                        id_tarea SERIAL PRIMARY KEY,
                        titulo VARCHAR(150) NOT NULL,
                        descripcion TEXT,
                        id_profesor INT NOT NULL,
                        CONSTRAINT fk_profesor FOREIGN KEY (id_profesor) REFERENCES usuarios(id)
);

CREATE TABLE entregas (
                          id_entrega SERIAL PRIMARY KEY,
                          id_tarea INT NOT NULL,
                          id_estudiante INT NOT NULL,
                          calificacion DECIMAL(5,2),
                          CONSTRAINT fk_tarea FOREIGN KEY (id_tarea) REFERENCES tareas(id_tarea),
                          CONSTRAINT fk_estudiante FOREIGN KEY (id_estudiante) REFERENCES usuarios(id)
);

INSERT INTO usuarios (nombre, correo, password, rol) VALUES
                                                         ('Admin', 'admin@centro.edu.ec', '123456', 'ADMIN'),
                                                         ('Estudiante1', 'estudiante@centro.edu.ec', '123456', 'ESTUDIANTE');

INSERT INTO usuarios (nombre, correo, password, rol, especialidad) VALUES
    ('Profesor1', 'profe@centro.edu.ec', '123456', 'PROFESOR', 'Matemáticas');











