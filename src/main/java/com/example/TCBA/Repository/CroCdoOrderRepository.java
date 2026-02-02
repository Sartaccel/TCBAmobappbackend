package com.example.TCBA.Repository;

import com.example.TCBA.Entity.CroCdoOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CroCdoOrderRepository extends JpaRepository<CroCdoOrder, Long> {

    Optional<CroCdoOrder> findByEntryNumber(String entryNumber);

}

