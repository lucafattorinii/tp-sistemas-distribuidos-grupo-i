-- Migración inicial del esquema de la base de datos

-- =============================================
-- Tabla: roles
-- =============================================
CREATE TABLE IF NOT EXISTS roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nombre del rol (ADMIN, VOLUNTARIO, etc.)',
  description VARCHAR(255) COMMENT 'Descripción del rol',
  permissions JSON COMMENT 'Permisos específicos del rol en formato JSON',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'Usuario que creó el rol',
  updated_by BIGINT COMMENT 'Usuario que actualizó el rol por última vez',
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el rol está activo',
  INDEX idx_roles_name (name),
  INDEX idx_roles_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: users
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
  password_reset_token VARCHAR(64) COMMENT 'Token para restablecer contraseña',
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
  FULLTEXT INDEX ft_users_search (first_name, last_name, email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: event_status
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: events
-- =============================================
CREATE TABLE IF NOT EXISTS events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL COMMENT 'Nombre del evento',
  description TEXT COMMENT 'Descripción detallada del evento',
  event_type VARCHAR(100) COMMENT 'Tipo de evento (DONACION, RECOLECCION, ENTREGA, etc.)',
  start_datetime DATETIME NOT NULL COMMENT 'Fecha y hora de inicio del evento',
  end_datetime DATETIME COMMENT 'Fecha y hora de finalización del evento',
  location VARCHAR(255) COMMENT 'Ubicación del evento (dirección o lugar)',
  status_id INT NOT NULL COMMENT 'Estado actual del evento',
  max_volunteers INT COMMENT 'Número máximo de voluntarios permitidos',
  created_by BIGINT NOT NULL COMMENT 'ID del usuario que creó el evento',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el evento por última vez',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el evento está activo',
  FOREIGN KEY (status_id) REFERENCES event_status(id) ON DELETE RESTRICT,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
  INDEX idx_events_name (name),
  INDEX idx_events_type (event_type),
  INDEX idx_events_datetime (start_datetime, end_datetime),
  INDEX idx_events_status (status_id),
  INDEX idx_events_created_by (created_by),
  INDEX idx_events_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: event_participants
-- =============================================
CREATE TABLE IF NOT EXISTS event_participants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id BIGINT NOT NULL COMMENT 'ID del evento',
  user_id BIGINT NOT NULL COMMENT 'ID del usuario participante',
  role VARCHAR(50) COMMENT 'Rol del participante en el evento (ORGANIZADOR, VOLUNTARIO, etc.)',
  status VARCHAR(20) DEFAULT 'PENDIENTE' COMMENT 'Estado de la participación (PENDIENTE, CONFIRMADA, RECHAZADA)',
  notes TEXT COMMENT 'Notas adicionales',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el registro',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el registro',
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_event_participant (event_id, user_id),
  INDEX idx_event_participants_event (event_id),
  INDEX idx_event_participants_user (user_id),
  INDEX idx_event_participants_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: donations
-- =============================================
CREATE TABLE IF NOT EXISTS donations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  donor_name VARCHAR(255) COMMENT 'Nombre del donante (si no es un usuario registrado)',
  donor_user_id BIGINT COMMENT 'ID del usuario donante (si está registrado)',
  donation_type VARCHAR(50) NOT NULL COMMENT 'Tipo de donación (DINERO, ALIMENTOS, ROPA, OTROS)',
  description TEXT COMMENT 'Descripción detallada de la donación',
  estimated_value DECIMAL(12, 2) COMMENT 'Valor estimado de la donación',
  status VARCHAR(20) DEFAULT 'PENDIENTE' COMMENT 'Estado de la donación (PENDIENTE, APROBADA, RECHAZADA, ENTREGADA)',
  donation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha en que se realizó la donación',
  delivery_date TIMESTAMP NULL COMMENT 'Fecha en que se entregó la donación',
  event_id BIGINT COMMENT 'ID del evento relacionado (si aplica)',
  notes TEXT COMMENT 'Notas adicionales',
  created_by BIGINT COMMENT 'ID del usuario que registró la donación',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó la donación por última vez',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  is_active BOOLEAN DEFAULT TRUE COMMENT 'Indica si el registro está activo',
  FOREIGN KEY (donor_user_id) REFERENCES users(id) ON DELETE SET NULL,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE SET NULL,
  INDEX idx_donations_donor (donor_user_id),
  INDEX idx_donations_type (donation_type),
  INDEX idx_donations_status (status),
  INDEX idx_donations_date (donation_date),
  INDEX idx_donations_event (event_id),
  INDEX idx_donations_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla: donation_items
-- =============================================
CREATE TABLE IF NOT EXISTS donation_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  donation_id BIGINT NOT NULL COMMENT 'ID de la donación a la que pertenece este ítem',
  item_name VARCHAR(255) NOT NULL COMMENT 'Nombre del ítem donado',
  description TEXT COMMENT 'Descripción detallada del ítem',
  quantity DECIMAL(10, 2) NOT NULL DEFAULT 1 COMMENT 'Cantidad donada',
  unit_of_measure VARCHAR(20) DEFAULT 'UNIDAD' COMMENT 'Unidad de medida (KG, LITRO, UNIDAD, etc.)',
  estimated_unit_value DECIMAL(12, 2) COMMENT 'Valor estimado por unidad',
  total_estimated_value DECIMAL(12, 2) COMMENT 'Valor total estimado (cantidad * valor unitario)',
  status VARCHAR(20) DEFAULT 'PENDIENTE' COMMENT 'Estado del ítem (PENDIENTE, APROBADO, RECHAZADO, ENTREGADO)',
  notes TEXT COMMENT 'Notas adicionales',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT COMMENT 'ID del usuario que creó el registro',
  updated_by BIGINT COMMENT 'ID del usuario que actualizó el registro',
  FOREIGN KEY (donation_id) REFERENCES donations(id) ON DELETE CASCADE,
  INDEX idx_donation_items_donation (donation_id),
  INDEX idx_donation_items_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Datos iniciales
-- =============================================

-- Roles iniciales
INSERT IGNORE INTO roles (id, name, description, permissions, is_active) VALUES
(1, 'ADMIN', 'Administrador del sistema', '{"all": true}', TRUE),
(2, 'VOLUNTARIO', 'Voluntario de la organización', '{"events": {"view": true, "create": false, "edit": false, "delete": false}, "donations": {"view": true, "create": true}}', TRUE),
(3, 'DONANTE', 'Donante de la organización', '{"donations": {"view_own": true, "create": true}}', TRUE);

-- Estados de eventos
INSERT IGNORE INTO event_status (id, name, description, is_active) VALUES
(1, 'PLANEADO', 'Evento planeado pero aún no ha comenzado', TRUE),
(2, 'EN_CURSO', 'Evento actualmente en desarrollo', TRUE),
(3, 'COMPLETADO', 'Evento finalizado exitosamente', TRUE),
(4, 'CANCELADO', 'Evento cancelado', TRUE);

-- Usuario administrador (contraseña: admin123)
INSERT IGNORE INTO users (
  id, username, email, password_hash, first_name, last_name, 
  phone, address, role_id, is_active, is_email_verified,
  created_by, updated_by
) VALUES (
  1, 'admin', 'admin@empuje.org', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
  'Administrador', 'del Sistema', 
  '1234567890', 'Oficina Central', 1, TRUE, TRUE,
  1, 1
);

-- Usuario voluntario (contraseña: voluntario123)
INSERT IGNORE INTO users (
  id, username, email, password_hash, first_name, last_name, 
  phone, address, role_id, is_active, is_email_verified,
  created_by, updated_by
) VALUES (
  2, 'voluntario1', 'voluntario@empuje.org', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
  'Juan', 'Pérez', 
  '0987654321', 'Calle Falsa 123', 2, TRUE, TRUE,
  1, 1
);

-- Usuario donante (contraseña: donante123)
INSERT IGNORE INTO users (
  id, username, email, password_hash, first_name, last_name, 
  phone, address, role_id, is_active, is_email_verified,
  created_by, updated_by
) VALUES (
  3, 'donante1', 'donante@empuje.org', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
  'María', 'González', 
  '0987654322', 'Av. Principal 456', 3, TRUE, TRUE,
  1, 1
);