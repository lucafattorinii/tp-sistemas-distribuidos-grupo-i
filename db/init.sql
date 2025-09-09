-- Eliminar la base de datos si existe y crearla de nuevo
DROP DATABASE IF EXISTS empuje;
CREATE DATABASE empuje;

USE empuje;

-- Configuración de SQL para un mejor rendimiento
SET SQL_MODE = 'NO_ENGINE_SUBSTITUTION';
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- Tabla: roles
-- Almacena los diferentes roles de los usuarios
-- =============================================
CREATE TABLE IF NOT EXISTS roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nombre del rol (PRESIDENTE, VOCAL, COORDINADOR, VOLUNTARIO)',
  description VARCHAR(255) COMMENT 'Descripción del rol',
  permissions JSON COMMENT 'Permisos específicos del rol en formato JSON',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'Usuario que creó el rol',
  updated_by BIGINT COMMENT 'Usuario que actualizó el rol por última vez',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el rol está activo',
  INDEX idx_roles_name (name),
  INDEX idx_roles_active (is_active),
  INDEX idx_roles_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla de roles de usuarios del sistema';

-- Insertar roles básicos
INSERT IGNORE INTO roles (id, name, description, permissions, is_active) VALUES
(1, 'ROLE_ADMIN', 'Administrador del sistema con todos los permisos', '{"all": true}', TRUE),
(2, 'ROLE_VOLUNTARIO', 'Voluntario con permisos limitados', '{"events": {"view": true, "create": false, "edit": false, "delete": false}, "inventory": {"view": true, "edit": false}}', TRUE),
(3, 'ROLE_DONANTE', 'Donante con permisos para ver y gestionar sus donaciones', '{"donations": {"view": true, "create": true, "edit_own": true, "delete_own": true}, "profile": {"view": true, "edit": true}}', TRUE);

-- =============================================
-- Tabla: users
-- Almacena la información de los usuarios del sistema
-- =============================================
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nombre de usuario para iniciar sesión',
  email VARCHAR(255) NOT NULL COMMENT 'Correo electrónico del usuario',
  password_hash VARCHAR(255) NOT NULL COMMENT 'Hash de la contraseña (bcrypt)',
  first_name VARCHAR(100) NOT NULL COMMENT 'Nombres del usuario',
  last_name VARCHAR(100) NOT NULL COMMENT 'Apellidos del usuario',
  dni VARCHAR(20) UNIQUE COMMENT 'Documento Nacional de Identidad',
  phone VARCHAR(20) COMMENT 'Número de teléfono',
  address TEXT COMMENT 'Dirección del usuario',
  profile_image VARCHAR(255) COMMENT 'URL de la imagen de perfil',
  role_id INT NOT NULL COMMENT 'Rol del usuario (referencia a la tabla roles)',
  last_login DATETIME COMMENT 'Último inicio de sesión',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el usuario está activo',
  is_email_verified BOOLEAN DEFAULT FALSE COMMENT 'Indica si el correo fue verificado',
  verification_token VARCHAR(64) COMMENT 'Token para verificación de correo',
  password_reset_token VARCHAR(64) COMMENT 'Token para restablecer contrña',
  password_reset_expires DATETIME COMMENT 'Fecha de expiración del token de restablecimiento',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó este registro',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó este registro',
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
  UNIQUE INDEX uq_users_email (email),
  INDEX idx_users_username (username),
  INDEX idx_users_dni (dni),
  INDEX idx_users_role (role_id),
  INDEX idx_users_active (is_active),
  INDEX idx_users_created_at (created_at),
  INDEX idx_users_fulltext (first_name, last_name),
  FULLTEXT INDEX ft_users_search (first_name, last_name, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabla de usuarios del sistema';

-- =============================================
-- Datos iniciales: Usuarios del sistema
-- =============================================
-- Insertar usuario administrador (contraseña: admin123)
INSERT IGNORE INTO users (
  id, username, email, password_hash, first_name, last_name, 
  phone, address, role_id, is_active, is_email_verified,
  created_by, updated_by
) VALUES (
  1, 'admin', 'admin@empuje.org', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
  'Administrador', 'del Sistema', 
  '1234567890', 'Oficina Central', 
  (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1), 
  TRUE, TRUE, 1, 1
);

-- Insertar usuario voluntario (contraseña: voluntario123)
INSERT IGNORE INTO users (
  id, username, email, password_hash, first_name, last_name, 
  phone, address, role_id, is_active, is_email_verified,
  created_by, updated_by
) VALUES (
  2, 'voluntario1', 'voluntario@empuje.org', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
  'Juan', 'Pérez', 
  '0987654321', 'Calle Falsa 123', 
  (SELECT id FROM roles WHERE name = 'ROLE_VOLUNTARIO' LIMIT 1), 
  TRUE, TRUE, 1, 1
);

-- =============================================
-- Tabla: inventory_categories
-- Categorías de ítems del inventario
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nombre de la categoría (ROPA, ALIMENTOS, etc.)',
  description TEXT COMMENT 'Descripción de la categoría',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si la categoría está activa',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó la categoría',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó la categoría',
  INDEX idx_inventory_categories_name (name) USING BTREE,
  INDEX idx_inventory_categories_active (is_active) USING BTREE,
  INDEX idx_inventory_categories_created_at (created_at) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Categorías de ítems del inventario';

-- =============================================
-- Tabla: inventory_items
-- Ítems del inventario
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL COMMENT 'Nombre del ítem',
  description TEXT COMMENT 'Descripción detallada del ítem',
  category_id INT NOT NULL COMMENT 'Categoría del ítem',
  unit_of_measure VARCHAR(20) DEFAULT 'UNIDAD' COMMENT 'Unidad de medida (KG, LITRO, UNIDAD, etc.)',
  current_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT 'Cantidad actual en inventario',
  minimum_quantity DECIMAL(10, 2) DEFAULT 0 COMMENT 'Cantidad mínima deseada para alertas',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el ítem está activo',
  is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Indica si el ítem fue eliminado (borrado lógico)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el ítem',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el ítem',
  FOREIGN KEY (category_id) REFERENCES inventory_categories(id) ON DELETE RESTRICT,
  INDEX idx_inventory_items_name (name),
  INDEX idx_inventory_items_category (category_id),
  INDEX idx_inventory_items_active (is_active),
  INDEX idx_inventory_items_quantity (current_quantity),
  INDEX idx_inventory_items_created_at (created_at),
  FULLTEXT INDEX ft_inventory_items_search (name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ítems del inventario';

-- =============================================
-- Tabla: inventory_movements
-- Registro de movimientos de inventario (entradas/salidas)
-- =============================================
CREATE TABLE IF NOT EXISTS inventory_movements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_id BIGINT NOT NULL COMMENT 'Ítem del inventario',
  movement_type ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL COMMENT 'Tipo de movimiento',
  quantity DECIMAL(10, 2) NOT NULL COMMENT 'Cantidad movida (positiva para entradas, negativa para salidas)',
  previous_quantity DECIMAL(10, 2) NOT NULL COMMENT 'Cantidad antes del movimiento',
  new_quantity DECIMAL(10, 2) NOT NULL COMMENT 'Cantidad después del movimiento',
  movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del movimiento',
  reference_id BIGINT COMMENT 'ID de referencia (ej: donación, evento, etc.)',
  reference_type VARCHAR(50) COMMENT 'Tipo de referencia (DONATION, EVENT, ADJUSTMENT, etc.)',
  notes TEXT COMMENT 'Notas adicionales sobre el movimiento',
  created_by BIGINT COMMENT 'ID del usuario que realizó el movimiento',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (item_id) REFERENCES inventory_items(id) ON DELETE RESTRICT,
  INDEX idx_inventory_movements_item (item_id),
  INDEX idx_inventory_movements_date (movement_date),
  INDEX idx_inventory_movements_created_at (created_at),
  INDEX idx_inventory_movements_type (movement_type),
  INDEX idx_inventory_movements_reference (reference_type, reference_id),
  INDEX idx_inventory_movements_creator (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Registro de movimientos de inventario';

-- =============================================
-- Tabla: event_status
-- Estados posibles para los eventos
-- =============================================
CREATE TABLE IF NOT EXISTS event_status (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nombre del estado (PLANEADO, EN_CURSO, COMPLETADO, CANCELADO)',
  description VARCHAR(255) COMMENT 'Descripción del estado',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el estado está activo',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el estado',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el estado',
  INDEX idx_event_status_name (name),
  INDEX idx_event_status_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Estados posibles para los eventos';

-- =============================================
-- Tabla: events
-- Eventos solidarios organizados
-- =============================================
CREATE TABLE IF NOT EXISTS events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL COMMENT 'Nombre del evento',
  description TEXT COMMENT 'Descripción detallada del evento',
  event_type VARCHAR(100) COMMENT 'Tipo de evento (DONACION, RECOLECCION, ENTREGA, etc.)',
  start_datetime DATETIME NOT NULL COMMENT 'Fecha y hora de inicio del evento',
  end_datetime DATETIME COMMENT 'Fecha y hora de finalización del evento',
  location_name VARCHAR(255) COMMENT 'Nombre del lugar donde se realiza el evento',
  location_address TEXT COMMENT 'Dirección completa del lugar',
  latitude DECIMAL(10, 8) COMMENT 'Latitud para geolocalización',
  longitude DECIMAL(11, 8) COMMENT 'Longitud para geolocalización',
  status_id INT NOT NULL COMMENT 'Estado actual del evento',
  max_participants INT COMMENT 'Número máximo de participantes permitidos (opcional)',
  is_public BOOLEAN DEFAULT TRUE COMMENT 'Indica si el evento es público',
  requires_registration BOOLEAN DEFAULT FALSE COMMENT 'Indica si se requiere registro previo',
  notes TEXT COMMENT 'Notas adicionales sobre el evento',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el evento',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el evento por última vez',
  FOREIGN KEY (status_id) REFERENCES event_status(id) ON DELETE RESTRICT,
  INDEX idx_events_datetime (start_datetime, end_datetime),
  INDEX idx_events_status (status_id),
  INDEX idx_events_public (is_public),
  INDEX idx_events_type (event_type),
  INDEX idx_events_created_at (created_at),
  INDEX idx_events_location (location_name(100)),
  FULLTEXT INDEX ft_events_search (name, description, location_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Eventos solidarios organizados';

-- =============================================
-- Tabla: event_participants
-- Participantes en eventos
-- =============================================
CREATE TABLE IF NOT EXISTS event_participants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BIGINT NOT NULL COMMENT 'Evento al que asiste',
  user_id BIGINT NOT NULL COMMENT 'Usuario participante',
  role VARCHAR(50) COMMENT 'Rol del participante en el evento (ORGANIZADOR, VOLUNTARIO, ASISTENTE, etc.)',
  status ENUM('CONFIRMADO', 'PENDIENTE', 'CANCELADO', 'ASISTIO', 'NO_ASISTIO') DEFAULT 'PENDIENTE' COMMENT 'Estado de la participación',
  notes TEXT COMMENT 'Notas adicionales',
  check_in DATETIME COMMENT 'Fecha y hora de registro de asistencia',
  check_out DATETIME COMMENT 'Fecha y hora de salida del evento',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que registró la participación',
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_event_user (event_id, user_id),
  INDEX idx_event_participants_event (event_id),
  INDEX idx_event_participants_user (user_id),
  INDEX idx_event_participants_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Participantes en eventos';

-- =============================================
-- Tabla: event_donations
-- Donaciones asociadas a eventos
-- =============================================
CREATE TABLE IF NOT EXISTS event_donations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BIGINT NOT NULL COMMENT 'Evento al que pertenece la donación',
  donor_name VARCHAR(255) COMMENT 'Nombre del donante (si no es un usuario registrado)',
  donor_user_id BIGINT COMMENT 'ID del usuario donante (si está registrado)',
  donation_type ENUM('DINERO', 'INSUMOS', 'SERVICIO', 'OTRO') NOT NULL COMMENT 'Tipo de donación',
  description TEXT COMMENT 'Descripción detallada de la donación',
  estimated_value DECIMAL(12, 2) COMMENT 'Valor estimado de la donación',
  received_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Fecha y hora en que se recibió la donación',
  received_by BIGINT COMMENT 'ID del usuario que registró la recepción',
  status ENUM('PENDIENTE', 'RECIBIDO', 'EN_PROCESO', 'ENTREGADO', 'RECHAZADO') DEFAULT 'PENDIENTE' COMMENT 'Estado de la donación',
  notes TEXT COMMENT 'Notas adicionales',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el registro',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el registro',
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY (donor_user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_event_donations_event (event_id),
  INDEX idx_event_donations_donor (donor_user_id),
  INDEX idx_event_donations_status (status),
  INDEX idx_event_donations_type (donation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Donaciones asociadas a eventos';

-- =============================================
-- Tabla: event_donation_items
-- Ítems específicos de donaciones en eventos
-- =============================================
CREATE TABLE IF NOT EXISTS event_donation_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  donation_id BIGINT NOT NULL COMMENT 'Donación a la que pertenece el ítem',
  item_id BIGINT COMMENT 'ID del ítem del inventario (si aplica)',
  item_name VARCHAR(255) NOT NULL COMMENT 'Nombre del ítem donado',
  quantity DECIMAL(10, 2) NOT NULL COMMENT 'Cantidad donada',
  unit_of_measure VARCHAR(20) DEFAULT 'UNIDAD' COMMENT 'Unidad de medida',
  description TEXT COMMENT 'Descripción adicional del ítem',
  estimated_value DECIMAL(12, 2) COMMENT 'Valor estimado por unidad',
  total_estimated_value DECIMAL(12, 2) GENERATED ALWAYS AS (quantity * COALESCE(estimated_value, 0)) STORED COMMENT 'Valor total estimado',
  status ENUM('PENDIENTE', 'RECIBIDO', 'EN_INVENTARIO', 'ENTREGADO', 'DONADO', 'PERDIDO') DEFAULT 'PENDIENTE' COMMENT 'Estado del ítem',
  notes TEXT COMMENT 'Notas adicionales',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el registro',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el registro',
  FOREIGN KEY (donation_id) REFERENCES event_donations(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES inventory_items(id) ON DELETE SET NULL,
  INDEX idx_event_donation_items_donation (donation_id),
  INDEX idx_event_donation_items_item (item_id),
  INDEX idx_event_donation_items_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Ítems específicos de donaciones en eventos';

-- =============================================
-- Datos iniciales: Estados de eventos
-- =============================================
-- Usar INSERT IGNORE para evitar errores de duplicados
INSERT IGNORE INTO event_status (id, name, description, is_active, created_by, updated_by) VALUES
(1, 'PLANEADO', 'El evento está siendo planeado', TRUE, 1, 1),
(2, 'CONFIRMADO', 'El evento está confirmado y listo para realizarse', TRUE, 1, 1),
(3, 'EN_CURSO', 'El evento está en desarrollo', TRUE, 1, 1),
(4, 'COMPLETADO', 'El evento ha finalizado exitosamente', TRUE, 1, 1),
(5, 'CANCELADO', 'El evento fue cancelado', TRUE, 1, 1),
(6, 'POSPUESTO', 'El evento fue pospuesto para otra fecha', TRUE, 1, 1);

-- =============================================
-- Datos iniciales: Categorías de inventario
-- =============================================
INSERT INTO inventory_categories (name, description, is_active, created_by) VALUES
('ALIMENTOS', 'Alimentos no perecederos y perecederos', TRUE, 1),
('ROPA', 'Ropa para todas las edades y géneros', TRUE, 1),
('HIGIENE', 'Artículos de higiene personal', TRUE, 1),
('MEDICAMENTOS', 'Medicamentos básicos y primeros auxilios', TRUE, 1),
('UTILES_ESCOLARES', 'Materiales educativos y escolares', TRUE, 1),
('OTROS', 'Otras categorías no especificadas', TRUE, 1);

-- =============================================
-- Datos iniciales: Ítems de inventario de ejemplo
-- =============================================
INSERT INTO inventory_items (name, description, category_id, unit_of_measure, current_quantity, minimum_quantity, is_active, created_by) VALUES
('Arroz', 'Arroz blanco en paquetes de 1kg', 1, 'UNIDAD', 50, 20, TRUE, 1),
('Frijoles', 'Frijoles negros en paquetes de 1kg', 1, 'UNIDAD', 30, 15, TRUE, 1),
('Aceite', 'Aceite vegetal en botellas de 1 litro', 1, 'LITRO', 25, 10, TRUE, 1),
('Leche en polvo', 'Leche en polvo fortificada', 1, 'KG', 15, 5, TRUE, 1),
('Atún enlatado', 'Lata de atún de 170g', 1, 'UNIDAD', 100, 30, TRUE, 1),
('Jabón de baño', 'Jabón de baño de glicerina', 3, 'UNIDAD', 80, 40, TRUE, 1),
('Pasta dental', 'Pasta dental de menta', 3, 'UNIDAD', 45, 20, TRUE, 1),
('Toallas higiénicas', 'Paquete de 10 unidades', 3, 'PAQUETE', 30, 15, TRUE, 1),
('Pañales T4', 'Pañales talla 4', 3, 'PAQUETE', 20, 10, TRUE, 1),
('Chamarra para niño', 'Chamarra para niño talla 8-10 años', 2, 'UNIDAD', 15, 5, TRUE, 1);

-- =============================================
-- Datos iniciales: Eventos de ejemplo
-- =============================================
-- Insertar estados primero si no existen
SET @estado_planeado = (SELECT id FROM event_status WHERE name = 'PLANEADO' LIMIT 1);
SET @estado_confirmado = (SELECT id FROM event_status WHERE name = 'CONFIRMADO' LIMIT 1);

-- Evento 1: Recolección de alimentos
INSERT INTO events (
  name, description, event_type, start_datetime, end_datetime, 
  location_name, location_address, status_id, max_participants, 
  is_public, requires_registration, created_by
) VALUES (
  'Campaña de Recolección de Alimentos', 
  'Recolección de alimentos no perecederos para familias necesitadas', 
  'RECOLECCION', 
  DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) + INTERVAL '09:00' HOUR_MINUTE, 
  DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) + INTERVAL '18:00' HOUR_MINUTE,
  'Plaza Central', 
  'Av. Principal #123, Colonia Centro', 
  @estado_confirmado, 
  15, 
  TRUE, 
  TRUE, 
  1
);

-- Evento 2: Entrega de despensas
INSERT INTO events (
  name, description, event_type, start_datetime, end_datetime, 
  location_name, location_address, status_id, max_participants, 
  is_public, requires_registration, created_by
) VALUES (
  'Entrega de Despensas Familiares', 
  'Entrega de despensas a familias en situación vulnerable', 
  'ENTREGA', 
  DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY) + INTERVAL '10:00' HOUR_MINUTE, 
  DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY) + INTERVAL '14:00' HOUR_MINUTE,
  'Comedor Comunitario', 
  'Calle Benito Juárez #456, Colonia Popular', 
  @estado_planeado, 
  10, 
  FALSE, 
  TRUE, 
  1
);

-- Evento 3: Taller de higiene
INSERT INTO events (
  name, description, event_type, start_datetime, end_datetime, 
  location_name, location_address, status_id, max_participants, 
  is_public, requires_registration, created_by
) VALUES (
  'Taller de Higiene Personal', 
  'Taller educativo sobre hábitos de higiene personal para niños', 
  'TALLER', 
  DATE_ADD(CURRENT_DATE, INTERVAL 21 DAY) + INTERVAL '16:00' HOUR_MINUTE, 
  DATE_ADD(CURRENT_DATE, INTERVAL 21 DAY) + INTERVAL '18:00' HOUR_MINUTE,
  'Escuela Primaria Benito Juárez', 
  'Calle Hidalgo #789, Colonia Centro', 
  @estado_confirmado, 
  30, 
  TRUE, 
  TRUE, 
  1
);

-- =============================================
-- Datos iniciales: Participantes en eventos
-- =============================================
-- Asegurarse de que exista al menos un usuario con ID 2 (voluntario)
-- Si no existe, se puede crear uno o ajustar estos valores
INSERT INTO event_participants (event_id, user_id, role, status, created_by) VALUES
(1, 1, 'ORGANIZADOR', 'CONFIRMADO', 1),  -- Admin como organizador
(1, 2, 'VOLUNTARIO', 'CONFIRMADO', 1),   -- Voluntario 1
(2, 1, 'ORGANIZADOR', 'CONFIRMADO', 1),  -- Admin como organizador
(3, 1, 'ORGANIZADOR', 'CONFIRMADO', 1),  -- Admin como organizador
(3, 2, 'FACILITADOR', 'CONFIRMADO', 1);  -- Voluntario como facilitador

-- =============================================
-- Datos iniciales: Donaciones de ejemplo
-- =============================================
-- Donación 1: Donación en efectivo para el evento de recolección
INSERT INTO event_donations (
  event_id, donor_name, donor_user_id, donation_type, 
  description, estimated_value, status, created_by
) VALUES (
  1, 
  'Empresa Solidaria S.A.', 
  NULL, 
  'DINERO', 
  'Donación en efectivo para la compra de alimentos', 
  5000.00, 
  'RECIBIDO', 
  1
);

-- Donación 2: Donación de insumos para el taller de higiene
INSERT INTO event_donations (
  event_id, donor_name, donor_user_id, donation_type, 
  description, estimated_value, status, created_by
) VALUES (
  3, 
  'Farmacia del Pueblo', 
  NULL, 
  'INSUMOS', 
  'Kits de higiene personal para el taller', 
  1200.00, 
  'ENTREGADO', 
  1
);

-- Donación 3: Donación de un usuario registrado
INSERT INTO event_donations (
  event_id, donor_name, donor_user_id, donation_type, 
  description, estimated_value, status, created_by
) VALUES (
  1, 
  NULL, 
  2, 
  'INSUMOS', 
  'Donación de alimentos no perecederos', 
  350.00, 
  'PENDIENTE', 
  2
);

-- =============================================
-- Datos iniciales: Ítems de donación
-- =============================================
-- Ítems para la donación 2 (kits de higiene)
INSERT INTO event_donation_items (
  donation_id, item_name, quantity, unit_of_measure, 
  description, estimated_value, status, created_by
) VALUES 
(2, 'Kit de Higiene Básica', 30, 'UNIDAD', 'Incluye jabón, cepillo dental y pasta dental', 40.00, 'ENTREGADO', 1);

-- Ítems para la donación 3 (alimentos)
INSERT INTO event_donation_items (
  donation_id, item_name, quantity, unit_of_measure, 
  description, estimated_value, status, created_by
) VALUES 
(3, 'Arroz', 10, 'KG', 'Arroz blanco', 15.00, 'PENDIENTE', 2),
(3, 'Frijoles', 5, 'KG', 'Frijoles negros', 12.50, 'PENDIENTE', 2),
(3, 'Aceite', 5, 'LITRO', 'Aceite vegetal', 10.00, 'PENDIENTE', 2);

-- =============================================
-- Datos iniciales: Movimientos de inventario
-- =============================================
-- Movimiento 1: Entrada de kits de higiene
INSERT INTO inventory_movements (
  item_id, movement_type, quantity, previous_quantity, new_quantity, 
  reference_type, reference_id, notes, created_by
) VALUES (
  (SELECT id FROM inventory_items WHERE name = 'Jabón de baño' LIMIT 1),
  'ENTRADA',
  80,
  0,
  80,
  'DONATION',
  2,
  'Entrada por donación de Farmacia del Pueblo',
  1
);

-- Movimiento 2: Entrada de pasta dental
INSERT INTO inventory_movements (
  item_id, movement_type, quantity, previous_quantity, new_quantity, 
  reference_type, reference_id, notes, created_by
) VALUES (
  (SELECT id FROM inventory_items WHERE name = 'Pasta dental' LIMIT 1),
  'ENTRADA',
  45,
  0,
  45,
  'DONATION',
  2,
  'Entrada por donación de Farmacia del Pueblo',
  1
);

-- =============================================
-- Actualización de cantidades actuales en inventario
-- =============================================
-- Actualizar las cantidades actuales basadas en los movimientos
UPDATE inventory_items i
JOIN (
  SELECT 
    item_id,
    COALESCE(SUM(
      CASE 
        WHEN movement_type = 'ENTRADA' THEN quantity 
        WHEN movement_type = 'SALIDA' THEN -quantity 
        ELSE 0 
      END
    ), 0) as total_quantity
  FROM inventory_movements
  GROUP BY item_id
) m ON i.id = m.item_id
SET i.current_quantity = m.total_quantity;
-- =============================================
-- Trigger para auditoría de usuarios (ejemplo, descomentar si es necesario)
-- =============================================
-- DELIMITER //
-- CREATE TRIGGER after_user_update
-- AFTER UPDATE ON users
-- FOR EACH ROW
-- BEGIN
--   -- Registrar cambios en usuarios (excepto en campos sensibles como contraseñas)
--   IF (OLD.email != NEW.email OR OLD.is_active != NEW.is_active OR OLD.role_id != NEW.role_id) THEN
--     -- Código del trigger aquí
--     -- Ejemplo de inserción en tabla de auditoría:
--     -- INSERT INTO audit_log (user_id, action, entity_type, entity_id, 
--     --   old_values, new_values, ip_address, user_agent)
--     -- VALUES (
--     --   NULLIF(NEW.updated_by, 0), 'UPDATE', 'USER', NEW.id,
--     --   JSON_OBJECT(
--     --     'email', OLD.email,
--     --     'is_active', OLD.is_active,
--     --     'role_id', OLD.role_id,
--     --     'updated_at', OLD.updated_at
--     --   ),
--     --   JSON_OBJECT(
--     --     'email', NEW.email,
--     --     'is_active', NEW.is_active,
--     --     'role_id', NEW.role_id,
--     --     'updated_at', NEW.updated_at
--     --   ),
--     --   NULL, -- IP del usuario de la sesión
--     --   NULL  -- User agent de la solicitud
--     -- );
--   END IF;
-- END //
-- DELIMITER ;
END //

-- =============================================
-- Triggers para actualización automática de timestamps
-- =============================================

-- Trigger para actualizar automáticamente updated_at en inventory_items
DELIMITER //
CREATE TRIGGER before_inventory_items_update
BEFORE UPDATE ON inventory_items
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger para actualizar automáticamente updated_at en events
CREATE TRIGGER before_events_update
BEFORE UPDATE ON events
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger para validar fechas de eventos futuras
CREATE TRIGGER before_events_insert
BEFORE INSERT ON events
FOR EACH ROW
BEGIN
    IF NEW.start_datetime < CURRENT_TIMESTAMP THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La fecha de inicio del evento debe ser futura';
    END IF;
    
    IF NEW.end_datetime IS NOT NULL AND NEW.end_datetime < NEW.start_datetime THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La fecha de finalización debe ser posterior a la de inicio';
    END IF;
END//

-- Trigger para validar actualizaciones de fechas de eventos
CREATE TRIGGER before_events_update_dates
BEFORE UPDATE ON events
FOR EACH ROW
BEGIN
    IF NEW.start_datetime < CURRENT_TIMESTAMP AND OLD.start_datetime >= CURRENT_TIMESTAMP THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede cambiar un evento pasado a una fecha anterior a la actual';
    END IF;
    
    IF NEW.end_datetime IS NOT NULL AND NEW.end_datetime < NEW.start_datetime THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La fecha de finalización debe ser posterior a la de inicio';
    END IF;
END//

-- =============================================
-- Vistas útiles para consultas frecuentes
-- =============================================

-- Vista para ver el inventario actual con nombres de categorías
CREATE OR REPLACE VIEW vw_current_inventory AS
SELECT 
    i.id,
    i.name,
    c.name AS category,
    i.description,
    i.current_quantity,
    i.unit_of_measure AS unit,
    i.minimum_quantity,
    i.is_active,
    CASE 
        WHEN i.current_quantity <= 0 THEN 'AGOTADO'
        WHEN i.current_quantity <= i.minimum_quantity THEN 'BAJO STOCK'
        ELSE 'DISPONIBLE'
    END AS status
FROM inventory_items i
JOIN inventory_categories c ON i.category_id = c.id
WHERE i.is_active = TRUE AND i.is_deleted = FALSE
ORDER BY c.name, i.name;

-- Vista para ver eventos próximos con conteo de participantes
CREATE OR REPLACE VIEW vw_upcoming_events AS
SELECT 
    e.id,
    e.name,
    e.description,
    e.event_type,
    e.start_datetime,
    e.end_datetime,
    e.location_name,
    e.status_id,
    es.name AS status_name,
    e.max_participants,
    COUNT(DISTINCT ep.user_id) AS current_participants,
    CONCAT(u.first_name, ' ', u.last_name) AS organizer_name,
    e.created_at
FROM events e
LEFT JOIN event_participants ep ON e.id = ep.event_id
JOIN users u ON e.created_by = u.id
JOIN event_status es ON e.status_id = es.id
WHERE e.start_datetime >= NOW()
AND e.status_id IN (SELECT id FROM event_status WHERE name IN ('PLANEADO', 'EN_CURSO'))
GROUP BY e.id, e.name, e.description, e.event_type, e.start_datetime, 
         e.end_datetime, e.location_name, e.status_id, es.name, 
         e.max_participants, organizer_name, e.created_at
ORDER BY e.start_datetime;

-- =============================================
-- Procedimientos almacenados útiles
-- =============================================

-- Procedimiento para registrar una nueva donación y actualizar el inventario
DELIMITER //
CREATE PROCEDURE register_donation(
    IN p_item_id BIGINT,
    IN p_quantity DECIMAL(10, 2),
    IN p_donor_name VARCHAR(255),
    IN p_notes TEXT,
    IN p_created_by BIGINT
)
BEGIN
    DECLARE v_current_quantity DECIMAL(10, 2);
    DECLARE v_new_quantity DECIMAL(10, 2);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Obtener la cantidad actual
    SELECT current_quantity INTO v_current_quantity 
    FROM inventory_items 
    WHERE id = p_item_id AND is_active = TRUE AND is_deleted = FALSE
    FOR UPDATE;
    
    IF v_current_quantity IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El ítem especificado no existe o no está activo';
    END IF;
    
    -- Calcular nueva cantidad
    SET v_new_quantity = v_current_quantity + p_quantity;
    
    -- Registrar el movimiento de entrada
    INSERT INTO inventory_movements (
        item_id, 
        movement_type,
        quantity,
        previous_quantity,
        new_quantity,
        reference_type, 
        notes, 
        created_by
    ) VALUES (
        p_item_id,
        'ENTRADA',
        p_quantity,
        v_current_quantity,
        v_new_quantity,
        'DONACION',
        CONCAT('Donación de ', p_donor_name, '. ', IFNULL(p_notes, '')),
        p_created_by
    );
    
    -- Actualizar la cantidad en inventario
    UPDATE inventory_items 
    SET current_quantity = v_new_quantity,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = p_created_by
    WHERE id = p_item_id;
    
    COMMIT;
END//

-- Procedimiento para registrar asistencia a un evento
CREATE PROCEDURE register_event_attendance(
    IN p_event_id BIGINT,
    IN p_user_id BIGINT,
    IN p_check_in BOOLEAN,
    IN p_updated_by BIGINT
)
BEGIN
    DECLARE v_event_exists INT;
    DECLARE v_user_exists INT;
    DECLARE v_participation_exists INT;
    
    -- Verificar que el evento existe y está activo
    SELECT COUNT(*) INTO v_event_exists 
    FROM events 
    WHERE id = p_event_id 
    AND is_active = TRUE;
    
    IF v_event_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El evento especificado no existe o no está activo';
    END IF;
    
    -- Verificar que el usuario existe y está activo
    SELECT COUNT(*) INTO v_user_exists 
    FROM users 
    WHERE id = p_user_id 
    AND is_active = TRUE;
    
    IF v_user_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario especificado no existe o no está activo';
    END IF;
    
    -- Verificar que existe la participación
    SELECT COUNT(*) INTO v_participation_exists 
    FROM event_participants 
    WHERE event_id = p_event_id 
    AND user_id = p_user_id;
    
    IF v_participation_exists = 0 THEN
        -- Si no existe, crear el registro de participación
        INSERT INTO event_participants (
            event_id, 
            user_id, 
            check_in,
            status,
            created_by,
            updated_by
        ) VALUES (
            p_event_id,
            p_user_id,
            CASE WHEN p_check_in THEN CURRENT_TIMESTAMP ELSE NULL END,
            CASE WHEN p_check_in THEN 'ASISTIO' ELSE 'REGISTRADO' END,
            p_updated_by,
            p_updated_by
        );
    ELSE
        -- Si existe, actualizar según corresponda
        IF p_check_in THEN
            -- Registrar check-in
            UPDATE event_participants
            SET check_in = CURRENT_TIMESTAMP,
                status = 'ASISTIO',
                updated_at = CURRENT_TIMESTAMP,
                updated_by = p_updated_by
            WHERE event_id = p_event_id 
            AND user_id = p_user_id;
        ELSE
            -- Registrar check-out
            UPDATE event_participants
            SET check_out = CURRENT_TIMESTAMP,
                status = 'COMPLETADO',
                updated_at = CURRENT_TIMESTAMP,
                updated_by = p_updated_by
            WHERE event_id = p_event_id 
            AND user_id = p_user_id
            AND check_in IS NOT NULL; -- Solo permitir check-out si ya hizo check-in
        END IF;
    END IF;
END//

DELIMITER ;

-- =============================================
-- Restricciones de validación adicionales
-- =============================================

-- Validar formato de email
ALTER TABLE users 
ADD CONSTRAINT chk_email_format 
CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$');

-- Validar que el teléfono solo contenga números y caracteres permitidos
ALTER TABLE users
ADD CONSTRAINT chk_phone_format
CHECK (phone IS NULL OR phone REGEXP '^[0-9+\\-() ]+$');

-- Validar que las cantidades en inventario no sean negativas
ALTER TABLE inventory_items 
ADD CONSTRAINT chk_positive_quantity 
CHECK (current_quantity >= 0);

-- Validar que las cantidades en movimientos sean positivas
ALTER TABLE inventory_movements
ADD CONSTRAINT chk_positive_movement
CHECK (quantity > 0);

-- =============================================
-- Configuración final
-- =============================================

-- Habilitar las restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 1;

-- Mostrar mensaje de éxito
SELECT 'Base de datos configurada exitosamente' AS message;

-- =============================================
-- Crear vista para reporte de inventario
-- =============================================
CREATE OR REPLACE VIEW vw_inventory_report AS
SELECT 
  i.id,
  i.name AS item_name,
  i.description,
  ic.name AS category_name,
  i.current_quantity,
  i.minimum_quantity,
  i.unit_of_measure,
  CASE 
    WHEN i.current_quantity <= 0 THEN 'AGOTADO'
    WHEN i.current_quantity <= i.minimum_quantity THEN 'STOCK_BAJO'
    ELSE 'DISPONIBLE'
  END AS stock_status,
  i.is_active,
  i.created_at,
  i.updated_at
FROM inventory_items i
JOIN inventory_categories ic ON i.category_id = ic.id
WHERE i.is_deleted = FALSE
ORDER BY stock_status, ic.name, i.name;

-- =============================================
-- Crear vista para reporte de eventos próximos
-- =============================================
CREATE OR REPLACE VIEW vw_upcoming_events AS
SELECT 
  e.id,
  e.name AS event_name,
  e.event_type,
  e.start_datetime,
  e.end_datetime,
  e.location_name,
  es.name AS status,
  e.max_participants,
  (SELECT COUNT(*) FROM event_participants ep WHERE ep.event_id = e.id) AS registered_participants,
  e.is_public,
  CONCAT(u.first_name, ' ', u.last_name) AS organizer_name,
  e.created_at
FROM events e
JOIN event_status es ON e.status_id = es.id
LEFT JOIN users u ON e.created_by = u.id
WHERE e.start_datetime >= CURRENT_DATE()
  AND es.name IN ('PLANEADO', 'CONFIRMADO')
ORDER BY e.start_datetime ASC
LIMIT 20;

-- Insert initial roles
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'PRESIDENTE'), 
(2, 'VOCAL'), 
(3, 'COORDINADOR'), 
(4, 'VOLUNTARIO');

-- Insert default admin user (password: admin123)
-- Note: This is just for development. In production, use a secure method to create the first admin.
SET @admin_password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQMRh2.'; -- bcrypt hash for 'admin123'

INSERT IGNORE INTO users (username, first_name, last_name, email, password_hash, role_id, created_by, is_active) 
VALUES ('admin', 'Admin', 'User', 'admin@empuje.org', @admin_password, 1, 1, TRUE);

-- Create a function to generate random strings for testing
DELIMITER //
CREATE FUNCTION IF NOT EXISTS random_string(length INT) 
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE chars VARCHAR(62) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    DECLARE result VARCHAR(255) DEFAULT '';
    DECLARE i INT DEFAULT 0;
    
    WHILE i < length DO
        SET result = CONCAT(result, SUBSTRING(chars, FLOOR(1 + RAND() * 62), 1));
        SET i = i + 1;
    END WHILE;
    
    RETURN result;
END //

-- Create a procedure to generate test data
CREATE PROCEDURE IF NOT EXISTS generate_test_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT;
    DECLARE event_count INT;
    DECLARE user_count INT;
    DECLARE inventory_count INT;
    
    -- Generate test users (20 users)
    SET user_count = 20;
    WHILE i <= user_count DO
        INSERT IGNORE INTO users (username, first_name, last_name, email, password_hash, role_id, is_active)
        VALUES (
            CONCAT('user', i),
            CONCAT('User', i),
            CONCAT('Lastname', i),
            CONCAT('user', i, '@example.com'),
            @admin_password, -- Using the same password hash for testing
            1 + FLOOR(RAND() * 4), -- Random role between 1 and 4
            TRUE
        );
        SET i = i + 1;
    END WHILE;
    
    -- Generate test inventory items (50 items)
    SET i = 1;
    SET inventory_count = 50;
    WHILE i <= inventory_count DO
        INSERT IGNORE INTO inventory (category, description, quantity, created_by)
        VALUES (
            ELT(1 + FLOOR(RAND() * 4), 'ROPA', 'ALIMENTOS', 'JUGUETES', 'UTILES_ESCOLARES'),
            CONCAT('Item ', i, ' - ', random_string(10)),
            FLOOR(10 + RAND() * 100),
            1 -- Created by admin
        );
        SET i = i + 1;
    END WHILE;
    
    -- Generate test events (15 events)
    SET i = 1;
    SET event_count = 15;
    WHILE i <= event_count DO
        INSERT IGNORE INTO events (name, description, event_datetime, created_by)
        VALUES (
            CONCAT('Evento ', i, ' - ', random_string(5)),
            CONCAT('Descripción del evento ', i, '. ', random_string(50)),
            DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 90) DAY),
            1 -- Created by admin
        );
        
        -- Add random participants to the event (2-5 participants)
        SET j = 1;
        SET @max_participants = 2 + FLOOR(RAND() * 4);
        WHILE j <= @max_participants DO
            INSERT IGNORE INTO event_participants (event_id, user_id)
            VALUES (LAST_INSERT_ID(), 1 + FLOOR(RAND() * user_count));
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
    
    -- Generate some past events with donations (5 events)
    SET i = 1;
    WHILE i <= 5 DO
        INSERT IGNORE INTO events (name, description, event_datetime, created_by)
        VALUES (
            CONCAT('Evento pasado ', i, ' - ', random_string(5)),
            CONCAT('Evento ya realizado el mes pasado. ', random_string(30)),
            DATE_SUB(NOW(), INTERVAL 30 + FLOOR(RAND() * 30) DAY),
            1 -- Created by admin
        );
        
        -- Add random participants to the past event
        SET j = 1;
        SET @max_participants = 2 + FLOOR(RAND() * 4);
        WHILE j <= @max_participants DO
            INSERT IGNORE INTO event_participants (event_id, user_id)
            VALUES (LAST_INSERT_ID(), 1 + FLOOR(RAND() * user_count));
            SET j = j + 1;
        END WHILE;
        
        -- Add some donations to the past event (1-3 items)
        SET j = 1;
        SET @donation_items = 1 + FLOOR(RAND() * 3);
        WHILE j <= @donation_items DO
            -- Primero insertamos la donación
            INSERT IGNORE INTO event_donations (
                event_id, 
                donor_name, 
                donation_type, 
                description, 
                status, 
                created_by, 
                updated_by
            ) VALUES (
                LAST_INSERT_ID(),
                CONCAT('Donante ', random_string(5)),
                ELT(1 + FLOOR(RAND() * 4), 'DINERO', 'INSUMOS', 'SERVICIO', 'OTRO'),
                CONCAT('Donación de prueba ', random_string(10)),
                'RECIBIDO',
                1, -- created_by admin
                1  -- updated_by admin
            );
            
            -- Luego insertamos el ítem de la donación
            INSERT IGNORE INTO event_donation_items (
                donation_id,
                item_id,
                quantity,
                unit_of_measure,
                description,
                status,
                created_by,
                updated_by
            ) VALUES (
                LAST_INSERT_ID(),
                1 + FLOOR(RAND() * inventory_count),
                1 + FLOOR(RAND() * 10),
                'UNIDAD',
                'Donación generada automáticamente',
                'RECIBIDO',
                1, -- created_by admin
                1  -- updated_by admin
            );
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
END //

-- Execute the test data generation (comment this out in production)
CALL generate_test_data();

DELIMITER ;
