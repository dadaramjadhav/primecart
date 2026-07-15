package com.primecart.repository;

import com.primecart.messaging.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByEventId(UUID eventId);

    Optional<ProcessedEvent> findByEventId(UUID eventId);
}