package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "instant_payment_requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "payment_request_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YardInstantPayoutRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", nullable = false, length = 50)
    private String entryNumber;

    @Column(name = "cha_id", nullable = false, length = 50)
    private String chaId;

    @Column(name = "gate_in_out_datetime")
    private LocalDateTime gateInOutDatetime;

    @Column(name = "yard_id", length = 50)
    private String yardId;

    @Column(name = "yard_gst_no", length = 30)
    private String yardGstNo;

    @Column(name = "container_nos")
    private String containerNos;

    @Column(name="container_size")
    private String containerSize;

    @Column(
            name = "payment_amount",
            precision = 12,
            scale = 2
    )
    private BigDecimal paymentAmount;

    @Column(name = "payment_type", length = 30)
    private String paymentType;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(
            name = "payment_request_id",
            nullable = false,
            unique = true,
            length = 100
    )

    private String paymentRequestId; // Idempotency key

    @Column(name = "request_received_at")
    private LocalDateTime requestReceivedAt;

    @Column(name = "verification_status", length = 20)
    private String verificationStatus; // VERIFIED / FAILED

    @Column(name = "payout_status", length = 20)
    private String payoutStatus; // SUCCESS / FAILED / PENDING

    @Column(name = "payout_id", length = 100)
    private String payoutId;

    @Column(name = "payout_notes", columnDefinition = "TEXT")
    private String payoutNotes;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

