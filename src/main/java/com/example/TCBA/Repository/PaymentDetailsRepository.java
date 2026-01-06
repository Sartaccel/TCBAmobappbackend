package com.example.TCBA.Repository;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentDetailsRepository
        extends JpaRepository<PaymentDetails, Long> {

    Optional<PaymentDetails> findTopByStackHolders(BrokerLogin broker);
}
