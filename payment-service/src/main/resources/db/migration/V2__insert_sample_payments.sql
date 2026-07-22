INSERT INTO payments
(payment_number, order_id, customer_id, amount, payment_method, status, transaction_id, created_at, updated_at)
VALUES ('PAY-100001', 1, '301f4e17-6208-4845-8c82-07bf6ea473d0', 999.00, 'CARD', 'SUCCESS', 'TXN100001', NOW(), NOW()),
       ('PAY-100002', 2, '301f4e17-6208-4845-8c82-07bf6ea473d0', 2499.00, 'UPI', 'SUCCESS', 'TXN100002', NOW(), NOW()),
       ('PAY-100003', 3, '301f4e17-6208-4845-8c82-07bf6ea473d0', 499.00, 'NET_BANKING', 'FAILED', 'TXN100003', NOW(),
        NOW()),
       ('PAY-100004', 4, '301f4e17-6208-4845-8c82-07bf6ea473d0', 799.00, 'CARD', 'PENDING', 'TXN100004', NOW(), NOW()),
       ('PAY-100005', 5, '301f4e17-6208-4845-8c82-07bf6ea473d0', 1299.00, 'UPI', 'SUCCESS', 'TXN100005', NOW(), NOW()),
       ('PAY-100006', 6, '301f4e17-6208-4845-8c82-07bf6ea473d0', 3499.00, 'CARD', 'SUCCESS', 'TXN100006', NOW(), NOW()),
       ('PAY-100007', 7, '301f4e17-6208-4845-8c82-07bf6ea473d0', 599.00, 'WALLET', 'FAILED', 'TXN100007', NOW(), NOW()),
       ('PAY-100008', 8, '301f4e17-6208-4845-8c82-07bf6ea473d0', 1599.00, 'UPI', 'SUCCESS', 'TXN100008', NOW(), NOW()),
       ('PAY-100009', 9, '301f4e17-6208-4845-8c82-07bf6ea473d0', 899.00, 'CARD', 'REFUNDED', 'TXN100009', NOW(), NOW()),
       ('PAY-100010', 10, '301f4e17-6208-4845-8c82-07bf6ea473d0', 2199.00, 'NET_BANKING', 'SUCCESS', 'TXN100010', NOW(),
        NOW());