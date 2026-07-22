package com.primecart.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<com.primecart.messaging.entity.ProcessedEvent, Long> {

    boolean existsByEventId(UUID eventId);
}