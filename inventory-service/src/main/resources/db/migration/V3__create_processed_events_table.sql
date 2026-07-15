CREATE TABLE processed_events (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  event_id VARCHAR(36) NOT NULL,
                                  event_type VARCHAR(100) NOT NULL,
                                  processed_at DATETIME NOT NULL,

                                  PRIMARY KEY (id),

                                  CONSTRAINT uk_processed_events_event_id
                                      UNIQUE (event_id)
);