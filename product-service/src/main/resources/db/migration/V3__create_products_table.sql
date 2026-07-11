CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10,2) NOT NULL,
                          image_url VARCHAR(500),
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          stock INT NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          category_id BIGINT NOT NULL,
                          brand_id BIGINT NOT NULL,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_product_category
                              FOREIGN KEY (category_id)
                                  REFERENCES categories(id),

                          CONSTRAINT fk_product_brand
                              FOREIGN KEY (brand_id)
                                  REFERENCES brands(id)
);