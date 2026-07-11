package com.primecart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number",
            nullable = false,
            unique = true,
            length = 50)
    private String paymentNumber;

    @Column(name = "order_id",
            nullable = false)
    private Long orderId;

    @Column(name = "customer_id",
            nullable = false)
    private String customerId;

    @Column(nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = 30)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id",
            unique = true,
            length = 100)
    private String transactionId;

    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}