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
INSERT INTO categories (id, name, parent_id, status) VALUES
(1, 'Điện tử', NULL, 'ACTIVE'),
(2, 'Laptop', 1, 'ACTIVE'),
(3, 'Gaming', 2, 'ACTIVE'),
(4, 'Phụ kiện', 1, 'ACTIVE');


INSERT INTO permissions (name, module_name, features, status)
VALUES
('CATEGORY_VIEW', 'CATEGORY', 'VIEW',   'ACTIVE'),
('CATEGORY_CREATE', 'CATEGORY', 'CREATE', 'ACTIVE'),
('CATEGORY_UPDATE', 'CATEGORY', 'UPDATE', 'ACTIVE'),
('CATEGORY_DELETE', 'CATEGORY', 'DELETE', 'ACTIVE'),
('PRODUCT_VIEW', 'PRODUCT', 'VIEW',   'ACTIVE'),
('PRODUCT_CREATE', 'PRODUCT', 'CREATE', 'ACTIVE'),
('PRODUCT_UPDATE', 'PRODUCT', 'UPDATE', 'ACTIVE'),
('PRODUCT_DELETE', 'PRODUCT', 'DELETE', 'ACTIVE');

INSERT INTO role_permissions (role_id, permission_id)
VALUES
(1, 1),  -- ADMIN -> CATEGORY_VIEW
(1, 2),
(1, 3),
(1, 4),
(1, 5),  -- ADMIN -> Product_VIEW
(1, 6),
(1, 7),
(1, 8),
(2, 1), -- CUSTOMER -> CATEGORY_VIEW
(3, 1); -- SELLER -> CATEGORY_VIEW


INSERT INTO products (id, name, description, status) VALUES
(1, 'Laptop Gaming ASUS ROG', 'Laptop gaming hiệu năng cao', 'APPROVED'),
(2, 'Laptop Văn Phòng Dell', 'Laptop mỏng nhẹ cho văn phòng', 'APPROVED'),
(3, 'Chuột Gaming Logitech', 'Chuột gaming RGB', 'APPROVED');

INSERT INTO product_categories (product_id, category_id) VALUES
(1, 3), -- ASUS ROG → Gaming
(2, 2), -- Dell → Laptop
(3, 4); -- Chuột → Phụ kiện

INSERT INTO product_variants (product_id, sku, price, stock, status) VALUES
-- ASUS ROG
(1, 'ROG-I7-16GB', 35000000.00, 10, 'ACTIVE'),
(1, 'ROG-I9-32GB', 45000000.00, 5,  'ACTIVE'),

-- Dell
(2, 'DELL-I5-8GB', 18000000.00, 20, 'ACTIVE'),
(2, 'DELL-I7-16GB', 25000000.00, 15, 'ACTIVE'),

-- Chuột
(3, 'LOGI-G102',   450000.00,   100, 'ACTIVE'),
(3, 'LOGI-G502',   1200000.00,  50,  'ACTIVE');
