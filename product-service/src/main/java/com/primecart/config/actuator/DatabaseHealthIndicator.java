package com.primecart.config.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);

            return Health.up()
                         .withDetail("productsTable", "Accessible")
                         .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}