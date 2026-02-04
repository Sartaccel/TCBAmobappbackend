package com.example.TCBA.Repository;

import com.example.TCBA.Entity.YardInstantPayoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YardInstantPayoutRequestRepository extends JpaRepository<YardInstantPayoutRequest,Long> {

    Optional<YardInstantPayoutRequest> findByEntryNumber(String entryNumber);
}
