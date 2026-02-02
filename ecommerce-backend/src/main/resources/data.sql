-- =========================
-- ROLES
-- =========================
INSERT IGNORE INTO roles (name, description) VALUES
('ADMIN', 'Quản trị hệ thống'),
('CUSTOMER', 'Khách hàng'),
('SELLER', 'Người bán');

-- =========================
-- USERS
-- password = 123456
-- =========================

INSERT IGNORE INTO users (username, email, password_hash, role_id, created_at)
VALUES (
    'admin',
    'admin@gmail.com',
    '$2a$10$aFVZOsuwi5IYKUn3c/smL.j58rVX0CWpXGpzkZu/L1uZIi.kQpM46',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    NOW()
);

INSERT IGNORE INTO users (username, email, password_hash, role_id, created_at)
VALUES (
    'customer1',
    'customer1@gmail.com',
    '$2a$10$aFVZOsuwi5IYKUn3c/smL.j58rVX0CWpXGpzkZu/L1uZIi.kQpM46',
    (SELECT id FROM roles WHERE name = 'CUSTOMER'),
    NOW()
);

INSERT IGNORE INTO users (username, email, password_hash, role_id, created_at)
VALUES (
    'seller1',
    'seller1@gmail.com',
    '$2a$10$aFVZOsuwi5IYKUn3c/smL.j58rVX0CWpXGpzkZu/L1uZIi.kQpM46',
    (SELECT id FROM roles WHERE name = 'SELLER'),
    NOW()
);
INSERT INTO categories (id, name, parent_id, status)
VALUES
(1, 'Điện tử', NULL, 'ACTIVE'),
(2, 'Laptop', 1, 'ACTIVE'),
(3, 'Gaming', 2, 'ACTIVE');


INSERT INTO permissions (name, roles_type, module_name, features, status)
VALUES
('CATEGORY_VIEW',   'ADMIN', 'CATEGORY', 'VIEW',   'ACTIVE'),
('CATEGORY_CREATE', 'ADMIN', 'CATEGORY', 'CREATE', 'ACTIVE'),
('CATEGORY_UPDATE', 'ADMIN', 'CATEGORY', 'UPDATE', 'ACTIVE'),
('CATEGORY_DELETE', 'ADMIN', 'CATEGORY', 'DELETE', 'ACTIVE');

INSERT INTO role_permissions (role_id, permission_id)
VALUES
(1, 1),  -- ADMIN -> CATEGORY_VIEW
(1, 2),
(1, 3),
(1, 4);