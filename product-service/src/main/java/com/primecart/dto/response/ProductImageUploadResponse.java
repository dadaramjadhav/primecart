package com.primecart.dto.response;

import java.net.URL;
import java.time.Instant;

public record ProductImageUploadResponse(String objectKey, URL uploadUrl, Instant expiresAt) {
}