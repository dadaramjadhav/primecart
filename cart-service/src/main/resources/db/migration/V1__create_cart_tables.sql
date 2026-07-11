CREATE TABLE carts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id VARCHAR(100) NOT NULL UNIQUE,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL
);

CREATE TABLE cart_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            product_name VARCHAR(255) NOT NULL,
                            quantity INT NOT NULL,
                            price DECIMAL(10,2) NOT NULL,
                            subtotal DECIMAL(10,2) NOT NULL,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,

                            CONSTRAINT fk_cart_items_cart
                                FOREIGN KEY (cart_id)
                                    REFERENCES carts(id)
                                    ON DELETE CASCADE
);
CREATE INDEX idx_cart_items_cart_id
    ON cart_items(cart_id);

CREATE INDEX idx_cart_items_product_id
    ON cart_items(product_id);