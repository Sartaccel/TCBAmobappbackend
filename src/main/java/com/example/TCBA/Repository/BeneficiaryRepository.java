package com.example.TCBA.Repository;

import com.example.TCBA.Entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    Optional<Beneficiary> findByUpiId(String upiId);
}


