package com.primecart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

@ConfigurationProperties(prefix = "primecart.storage")
public record ProductImageStorageProperties(String endpoint, String region, String bucket, boolean pathStyleAccess,
                                            String accessKey, String secretKey,
                                            Duration presignedUrlDuration, DataSize maxFileSize) {
}
