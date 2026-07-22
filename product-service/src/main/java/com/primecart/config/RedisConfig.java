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

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .prefixCacheNameWith("primecart::")
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache: Product by ID
        cacheConfigurations.put(CacheNames.PRODUCTS, defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Cache: All Products
        cacheConfigurations.put(CacheNames.ALL_PRODUCTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));

        cacheConfigurations.put(CacheNames.CATEGORIES, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        cacheConfigurations.put(CacheNames.BRANDS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig)

                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
