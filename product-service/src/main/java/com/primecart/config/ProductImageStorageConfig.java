package com.primecart.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(ProductImageStorageProperties.class)
public class ProductImageStorageConfig {

    @Bean
    S3Client s3Client(ProductImageStorageProperties properties) {
        S3ClientBuilder builder = S3Client
                .builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider(properties))
                .serviceConfiguration(s3Configuration(properties));

        if (StringUtils.hasText(properties.endpoint())) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }
        return builder.build();
    }

    @Bean
    S3Presigner s3Presigner(ProductImageStorageProperties properties) {
        Builder builder = S3Presigner
                .builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider(properties))
                .serviceConfiguration(s3Configuration(properties));

        if (StringUtils.hasText(properties.endpoint())) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }
        return builder.build();
    }

    private S3Configuration s3Configuration(ProductImageStorageProperties properties) {
        return S3Configuration
                .builder()
                .pathStyleAccessEnabled(properties.pathStyleAccess())
                .build();
    }

    private StaticCredentialsProvider credentialsProvider(ProductImageStorageProperties properties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.accessKey(),
                properties.secretKey());

        return StaticCredentialsProvider.create(credentials);
    }
}
