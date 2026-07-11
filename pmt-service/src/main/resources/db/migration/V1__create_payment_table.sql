CREATE TABLE payments (

                          id BIGINT AUTO_INCREMENT PRIMARY KEY,

                          payment_number VARCHAR(50) NOT NULL UNIQUE,

                          order_id BIGINT NOT NULL,

                          customer_id VARCHAR(255) NOT NULL,

                          amount DECIMAL(10,2) NOT NULL,

                          payment_method VARCHAR(30) NOT NULL,

                          status VARCHAR(30) NOT NULL,

                          transaction_id VARCHAR(100),

                          created_at DATETIME NOT NULL,

                          updated_at DATETIME NOT NULL

);

CREATE INDEX idx_payments_order_id
    ON payments(order_id);

CREATE INDEX idx_payments_customer_id
    ON payments(customer_id);

CREATE INDEX idx_payments_status
    ON payments(status);