CREATE TABLE processed_events
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id     VARCHAR(36)  NOT NULL,
    event_type   VARCHAR(100) NOT NULL,
    processed_at DATETIME     NOT NULL,

    CONSTRAINT uk_payment_processed_event_id
        UNIQUE (event_id)
);