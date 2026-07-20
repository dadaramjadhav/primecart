CREATE TABLE customer_profiles
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    keycloak_user_id   VARCHAR(100) NOT NULL,
    username           VARCHAR(100),
    email              VARCHAR(255),
    first_name         VARCHAR(100),
    last_name          VARCHAR(100),
    phone              VARCHAR(30),
    created_at         TIMESTAMP(6) NOT NULL,
    updated_at         TIMESTAMP(6) NOT NULL,

    CONSTRAINT uk_customer_profiles_keycloak_user_id
        UNIQUE (keycloak_user_id)
);