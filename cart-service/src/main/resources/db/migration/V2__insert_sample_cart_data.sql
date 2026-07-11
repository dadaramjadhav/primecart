INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
                                                            (1, 'user-1001', NOW(), NOW()),
                                                            (2, 'user-1002', NOW(), NOW()),
                                                            (3, 'user-1003', NOW(), NOW()),
                                                            (4, 'user-1004', NOW(), NOW()),
                                                            (5, 'user-1005', NOW(), NOW()),
                                                            (6, 'user-1006', NOW(), NOW()),
                                                            (7, 'user-1007', NOW(), NOW()),
                                                            (8, 'user-1008', NOW(), NOW()),
                                                            (9, 'user-1009', NOW(), NOW()),
                                                            (10, 'user-1010', NOW(), NOW());

INSERT INTO cart_items
(id, cart_id, product_id, product_name, quantity, price, subtotal, created_at, updated_at)
VALUES
    (1, 1, 101, 'iPhone 16 Pro',        1, 69999.00, 69999.00, NOW(), NOW()),
    (2, 2, 102, 'Samsung Galaxy S25',   2, 49999.00, 99998.00, NOW(), NOW()),
    (3, 3, 103, 'MacBook Air M4',       1, 89999.00, 89999.00, NOW(), NOW()),
    (4, 4, 104, 'Sony WH-1000XM5',      3, 1499.00, 4497.00, NOW(), NOW()),
    (5, 5, 105, 'Apple Watch Series 10',2, 2999.00, 5998.00, NOW(), NOW()),
    (6, 6, 106, 'Dell XPS 15',          1, 79999.00, 79999.00, NOW(), NOW()),
    (7, 7, 107, 'Logitech MX Master 3S',4, 999.00, 3996.00, NOW(), NOW()),
    (8, 8, 108, 'iPad Air M3',          2, 24999.00, 49998.00, NOW(), NOW()),
    (9, 9, 109, 'Kindle Paperwhite',    1, 12999.00, 12999.00, NOW(), NOW()),
    (10, 10, 110, 'JBL Flip 7',         5, 799.00, 3995.00, NOW(), NOW());