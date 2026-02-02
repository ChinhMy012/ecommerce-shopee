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
