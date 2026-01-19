package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payout_transaction_mob",
        indexes = {
                @Index(name = "idx_ref", columnList = "referenceId", unique = true),
                @Index(name = "idx_payout", columnList = "razorpayPayoutId")
        }
)
@Data
public class PayoutTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stackHolderId;
    private BigDecimal amount;

    @Column(nullable = false, unique = true)
    private String referenceId;

    @Column(name = "yard_name")
    private String yardName;

    private String razorpayPayoutId;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;

    private String payoutMode; // UPI / BANK
    private String upiId;
    private String bankAccountLast4;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String razorpayResponse;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PayoutStatus {
        CREATED,
        PROCESSING,
        SUCCESS,
        FAILED
    }
}

