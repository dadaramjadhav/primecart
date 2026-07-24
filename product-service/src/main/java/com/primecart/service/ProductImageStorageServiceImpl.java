package com.primecart.service;

import com.primecart.config.ProductImageStorageProperties;
import com.primecart.dto.request.CreateProductImageUploadRequest;
import com.primecart.dto.response.ProductImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageStorageServiceImpl implements ProductImageStorageService {

    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp",
                                                                                 "webp");

    private static final Set<String> ALLOWED_CONTENT_TYPES = EXTENSIONS_BY_CONTENT_TYPE.keySet();

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final ProductImageStorageProperties properties;

    @Override
    public ProductImageUploadResponse createUploadUrl(CreateProductImageUploadRequest request) {

        validateRequest(request);

        String extension = EXTENSIONS_BY_CONTENT_TYPE.get(request.contentType());
        String objectKey = "products/temporary/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(request.contentType())
                .contentLength(request.fileSize())
                .metadata(Map.of("original-filename", sanitizeMetadataValue(request.fileName())))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest
                .builder()
                .signatureDuration(properties.presignedUrlDuration())
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        Instant expiresAt = Instant
                .now()
                .plus(properties.presignedUrlDuration());

        return new ProductImageUploadResponse(objectKey, presignedRequest.url(), expiresAt);
    }

//    @Override
//    public ProductImageStoredResponse upload(MultipartFile file) {
//        validateFile(file);
//
//        String contentType = file.getContentType();
//        String objectKey = createObjectKey(contentType);
//
//        PutObjectRequest putObjectRequest = PutObjectRequest
//                .builder()
//                .bucket(properties.bucket())
//                .key(objectKey)
//                .contentType(contentType)
//                .contentLength(file.getSize())
//                .metadata(Map.of("original-filename", sanitizeMetadataValue(file.getOriginalFilename())))
//                .build();
//
//        try {
//            PutObjectResponse response = s3Client.putObject(
//                    putObjectRequest,
//                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//
//            return new ProductImageStoredResponse(objectKey, response.eTag());
//        } catch (IOException exception) {
//            throw new ResponseStatusException(
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    "Unable to read the uploaded image",
//                    exception);
//        } catch (S3Exception exception) {
//            String errorCode = exception.awsErrorDetails() == null
//                    ? "unknown"
//                    : exception.awsErrorDetails().errorCode();
//            String errorMessage = exception.awsErrorDetails() == null
//                    ? exception.getMessage()
//                    : exception.awsErrorDetails().errorMessage();
//
//            log.error(
//                    "S3 image upload failed: status={}, code={}, requestId={}, message={}",
//                    exception.statusCode(),
//                    errorCode,
//                    exception.requestId(),
//                    errorMessage);
//
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_GATEWAY,
//                    "Unable to store the image in S3",
//                    exception);
//        } catch (SdkClientException exception) {
//            log.error("Unable to connect to S3: {}", exception.getMessage());
//
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_GATEWAY,
//                    "Unable to connect to object storage",
//                    exception);
//        }
//    }

    private void validateRequest(CreateProductImageUploadRequest request) {
        validateContentType(request.contentType());

        if (request.fileSize() > properties
                .maxFileSize()
                .toBytes()) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds the maximum allowed size");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        validateContentType(file.getContentType());

        if (file.getSize() > properties
                .maxFileSize()
                .toBytes()) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds the maximum allowed size");
        }
    }

    private void validateContentType(String contentType) {
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG and WebP images are allowed");
        }
    }

    private String createObjectKey(String contentType) {
        return "products/temporary/" + UUID.randomUUID() + "." + EXTENSIONS_BY_CONTENT_TYPE.get(contentType);
    }

    private String sanitizeMetadataValue(String fileName) {
        if (fileName == null) {
            return "image";
        }

        return fileName
                .replaceAll("[\\r\\n]", "")
                .trim();
    }
}
