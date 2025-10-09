-- ============================================
-- Database: bd_restaurant
-- Tables: Employee, Roles, Employee_Roles
-- Description: Updated schema with role-based access control
-- ============================================

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS bd_restaurant;
USE bd_restaurant;

-- Create Employee table
CREATE TABLE IF NOT EXISTS employee (
    id_empleado BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    contrasenia VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY uk_employee_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Roles table
CREATE TABLE IF NOT EXISTS roles (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Employee_Roles junction table (Many-to-Many relationship)
CREATE TABLE IF NOT EXISTS employee_roles (
    id_empleado BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_empleado, id_rol),
    FOREIGN KEY (id_empleado) REFERENCES employee(id_empleado) ON DELETE CASCADE,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default roles
INSERT INTO roles (nombre_rol) VALUES 
('ROLE_ADMIN'),
('ROLE_WAITER'),
('ROLE_CHEF')
ON DUPLICATE KEY UPDATE nombre_rol = VALUES(nombre_rol);

-- Insert sample employees with BCrypt hashed password 'password123'
-- Hash: $2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u
INSERT INTO employee (nombre, apellido, contrasenia, enabled) VALUES
('admin', 'Administrator', '$2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u', TRUE),
('juan', 'Perez', '$2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u', TRUE),
('maria', 'Garcia', '$2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u', TRUE),
('carlos', 'Rodriguez', '$2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u', TRUE),
('ana', 'Martinez', '$2a$10$xHYH5U0AqKmYJnEqJbxh0e7n3/bL9/6KJVcR1gkHqrM8KVNGQqG9u', TRUE)
ON DUPLICATE KEY UPDATE 
    apellido = VALUES(apellido),
    contrasenia = VALUES(contrasenia),
    enabled = VALUES(enabled);

-- Assign roles to employees
-- Admin role to 'admin' user
INSERT INTO employee_roles (id_empleado, id_rol)
SELECT e.id_empleado, r.id_rol
FROM employee e, roles r
WHERE e.nombre = 'admin' AND r.nombre_rol = 'ROLE_ADMIN'
ON DUPLICATE KEY UPDATE id_empleado = VALUES(id_empleado);

-- Waiter role to 'juan' and 'maria'
INSERT INTO employee_roles (id_empleado, id_rol)
SELECT e.id_empleado, r.id_rol
FROM employee e, roles r
WHERE e.nombre = 'juan' AND r.nombre_rol = 'ROLE_WAITER'
ON DUPLICATE KEY UPDATE id_empleado = VALUES(id_empleado);

INSERT INTO employee_roles (id_empleado, id_rol)
SELECT e.id_empleado, r.id_rol
FROM employee e, roles r
WHERE e.nombre = 'maria' AND r.nombre_rol = 'ROLE_WAITER'
ON DUPLICATE KEY UPDATE id_empleado = VALUES(id_empleado);

-- Chef role to 'carlos' and 'ana'
INSERT INTO employee_roles (id_empleado, id_rol)
SELECT e.id_empleado, r.id_rol
FROM employee e, roles r
WHERE e.nombre = 'carlos' AND r.nombre_rol = 'ROLE_CHEF'
ON DUPLICATE KEY UPDATE id_empleado = VALUES(id_empleado);

INSERT INTO employee_roles (id_empleado, id_rol)
SELECT e.id_empleado, r.id_rol
FROM employee e, roles r
WHERE e.nombre = 'ana' AND r.nombre_rol = 'ROLE_CHEF'
ON DUPLICATE KEY UPDATE id_empleado = VALUES(id_empleado);

-- Display created data
SELECT 'Roles Created:' AS '';
SELECT * FROM roles;

SELECT 'Employees Created:' AS '';
SELECT id_empleado, nombre, apellido, enabled FROM employee;

SELECT 'Employee-Role Assignments:' AS '';
SELECT 
    e.id_empleado,
    e.nombre,
    e.apellido,
    r.nombre_rol
FROM employee e
JOIN employee_roles er ON e.id_empleado = er.id_empleado
JOIN roles r ON er.id_rol = r.id_rol
ORDER BY e.id_empleado;
