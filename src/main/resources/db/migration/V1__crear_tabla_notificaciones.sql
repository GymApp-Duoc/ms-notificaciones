CREATE TABLE notificaciones (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                miembro_id BIGINT NOT NULL,
                                titulo VARCHAR(100) NOT NULL,
                                mensaje VARCHAR(500) NOT NULL,
                                leida BOOLEAN NOT NULL DEFAULT FALSE,
                                fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);