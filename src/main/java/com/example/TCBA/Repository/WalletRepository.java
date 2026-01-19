package com.example.TCBA.Repository;

import com.example.TCBA.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository
        extends JpaRepository<Wallet, String> {

    Optional<Wallet> findByBrokerId(Long brokerId);

    Optional<Wallet> findByStakeHolderId(String stakeHolderId);
}

