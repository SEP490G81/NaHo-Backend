-- =========================================================
-- Insert Permissions
-- =========================================================

INSERT INTO permissions (created_time, modified_time, permission_code, description)
VALUES (NOW(), NULL, 'ADMIN', 'Admin permission'),
       (NOW(), NULL, 'ROLE_ADMIN', 'Role Admin permission'),
       (NOW(), NULL, 'LEARNER', 'Learner permission'),
       (NOW(), NULL, 'ROLE_LEARNER', 'Role Learner permission'),
       (NOW(), NULL, 'CONTENT_MANAGER', 'Content Manager permission'),
       (NOW(), NULL, 'ROLE_CONTENT_MANAGER', 'Role Content Manager permission');

-- =========================================================
-- Map Roles to Permissions
-- =========================================================

INSERT INTO roles_permissions (permission_id, role_id)
SELECT p.id, r.id
FROM permissions p
JOIN roles r ON r.role_name = 'ADMIN'
WHERE p.permission_code IN ('ADMIN', 'ROLE_ADMIN');

INSERT INTO roles_permissions (permission_id, role_id)
SELECT p.id, r.id
FROM permissions p
JOIN roles r ON r.role_name = 'LEARNER'
WHERE p.permission_code IN ('LEARNER', 'ROLE_LEARNER');

INSERT INTO roles_permissions (permission_id, role_id)
SELECT p.id, r.id
FROM permissions p
JOIN roles r ON r.role_name = 'CONTENT_MANAGER'
WHERE p.permission_code IN ('CONTENT_MANAGER', 'ROLE_CONTENT_MANAGER');
