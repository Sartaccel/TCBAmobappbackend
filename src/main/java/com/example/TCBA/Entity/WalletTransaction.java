package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transaction_mob"
        ,uniqueConstraints = {@UniqueConstraint(columnNames = "razorpay_payment_id"),
        @UniqueConstraint(columnNames = "transaction_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    public enum TxnType {
        CREDIT,
        DEBIT
    }

    public enum TxnStatus {
        CREATED,
        AUTHORIZED,
        CAPTURED,
        SUCCESS,
        FAILED
    }

    @Id
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // 🔥 MANY TXNS → ONE WALLET
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stake_holder_id",
            referencedColumnName = "stack_holder_id",
            nullable = false
    )
    private BrokerLogin stakeHolder;

    // Razorpay fields
    @Column(name = "razorpay_order_id", length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnType txnType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnStatus status;

    @CreationTimestamp
    private LocalDateTime createdOn;
}


