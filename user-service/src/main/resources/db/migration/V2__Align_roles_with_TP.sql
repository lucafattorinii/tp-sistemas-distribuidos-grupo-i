-- Align existing role names with TP requirements
-- Map previous roles to new ones: ROLE_ADMIN -> PRESIDENTE, ROLE_VOLUNTARIO -> VOLUNTARIO, ROLE_DONANTE -> VOCAL (temporary), adjust descriptions accordingly.

UPDATE roles SET name = 'PRESIDENTE', description = 'Presidente: gestiona usuarios y todas las acciones' WHERE name = 'ROLE_ADMIN';
UPDATE roles SET name = 'VOLUNTARIO', description = 'Voluntario: consulta eventos y participa' WHERE name = 'ROLE_VOLUNTARIO';
-- If previously used DONANTE as a role, map to VOCAL for access to inventario
UPDATE roles SET name = 'VOCAL', description = 'Vocal: gestiona inventario de donaciones' WHERE name = 'ROLE_DONANTE';

-- Ensure all four roles exist
INSERT IGNORE INTO roles (name, description, is_active) VALUES
('COORDINADOR', 'Coordina eventos solidarios', TRUE),
('PRESIDENTE', 'Presidente: gestiona usuarios y todas las acciones', TRUE),
('VOCAL', 'Vocal: gestiona inventario de donaciones', TRUE),
('VOLUNTARIO', 'Voluntario: consulta eventos y participa', TRUE);
