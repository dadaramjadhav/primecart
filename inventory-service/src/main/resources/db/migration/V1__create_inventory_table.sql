CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
                       );

INSERT INTO inventory (product_id, available_quantity, reserved_quantity, created_at, updated_at)
VALUES (1,100,0,NOW(),NOW()),
       (2,50,0,NOW(),NOW()),
       (3,75,5,NOW(),NOW()),
       (4,20,0,NOW(),NOW()),
       (5,200,10,NOW(),NOW()),
       (6,30,0,NOW(),NOW()),
       (7,90,15,NOW(),NOW()),
       (8,60,0,NOW(),NOW()),
       (9,40,5,NOW(),NOW()),
       (10,150,0,NOW(),NOW());