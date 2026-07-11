INSERT INTO orders (
    order_number,
    customer_id,
    status,
    total_amount,
    created_at,
    updated_at
)
VALUES
    (
        'ORD-20260710-00001',
        101,
        'CREATED',
        2499.97,
        NOW(),
        NOW()
    ),
    (
        'ORD-20260710-00002',
        102,
        'CONFIRMED',
        1799.98,
        NOW(),
        NOW()
    ),
    (
        'ORD-20260710-00003',
        103,
        'SHIPPED',
        999.99,
        NOW(),
        NOW()
    );

INSERT INTO order_items (
    order_id,
    product_id,
    product_name,
    price,
    quantity,
    subtotal
)
VALUES
    (
        1,
        1,
        'Apple iPhone 16',
        999.99,
        2,
        1999.98
    ),
    (
        1,
        5,
        '20W USB-C Charger',
        499.99,
        1,
        499.99
    ),
    (
        2,
        2,
        'Samsung Galaxy S25',
        899.99,
        2,
        1799.98
    ),
    (
        3,
        3,
        'Sony WH-1000XM6',
        999.99,
        1,
        999.99
    );