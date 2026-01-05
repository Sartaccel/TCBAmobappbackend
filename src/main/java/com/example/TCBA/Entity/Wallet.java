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
@Table(name = "wallet_mob")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @Column(name = "wallet_id", length = 100)
    private String walletId;

    // 🔐 BACKEND decides whose wallet
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "broker_id",
            nullable = false,
            unique = true
    )
    private BrokerLogin brokerLogin;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdOn;
}


