CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        order_number VARCHAR(50) NOT NULL UNIQUE,
                        customer_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        total_amount DECIMAL(10,2) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
);

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             price DECIMAL(10,2) NOT NULL,
                             quantity INT NOT NULL,
                             subtotal DECIMAL(10,2) NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE
);

CREATE INDEX idx_orders_order_number
    ON orders(order_number);

CREATE INDEX idx_orders_customer_id
    ON orders(customer_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);

CREATE INDEX idx_order_items_product_id
    ON order_items(product_id);