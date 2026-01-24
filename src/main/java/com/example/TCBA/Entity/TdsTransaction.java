package com.example.TCBA.Entity;

import com.example.TCBA.Entity.PayoutTransaction;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tds_transaction_mob")
@Data
public class TdsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentRequestId;

    private String payoutReferenceId;

    private String razorpayPayoutId;

    private String stackHolderId;

    private String yardName;
    private String entryNo;
    private String containerNo;

    private BigDecimal totalAmount;
    private BigDecimal tdsPercent;
    private BigDecimal tdsAmount;
    private BigDecimal netPaidAmount;

    @Enumerated(EnumType.STRING)
    private TdsStatus tdsStatus;

    @Enumerated(EnumType.STRING)
    private PayoutTransaction.PayoutStatus payoutStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TdsStatus {

        UNPAID,
        PAID
    }

    public enum PayoutStatus {

        CREATED,
        PROCESSING,
        SUCCESS,
        FAILED
    }
}
