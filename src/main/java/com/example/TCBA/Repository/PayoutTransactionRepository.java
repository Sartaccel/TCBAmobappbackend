package com.example.TCBA.Repository;


import com.example.TCBA.Entity.PayoutTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutTransactionRepository
        extends JpaRepository<PayoutTransaction, Long> {

    Optional<PayoutTransaction> findByReferenceId(String referenceId);
}
