package com.primecart.dto.response;

import java.time.LocalDateTime;

public record CustomerProfileResponse(Long id, String keycloakUserId, String username, String email, String firstName, String lastName,
                                      String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
}