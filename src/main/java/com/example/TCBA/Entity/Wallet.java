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
    @Column(name = "stake_holder_id")
    private String stakeHolderId;

    @Column(name = "broker_id")
    private Long brokerId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdOn;
}


