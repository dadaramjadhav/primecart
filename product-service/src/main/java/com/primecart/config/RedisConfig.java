package com.primecart.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                                                       .prefixCacheNameWith("primecart::")
                                                                       .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache: Product by ID
        cacheConfigurations.put(
                "products",
                defaultConfig.entryTtl(Duration.ofMinutes(10))
        );

        // Cache: All Products
        cacheConfigurations.put(
                "allProducts",
                defaultConfig.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultConfig)

                                .withInitialCacheConfigurations(cacheConfigurations)
                                .transactionAware()
                                .build();
    }
}