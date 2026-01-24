package com.example.TCBA.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.TCBA.Entity.TdsTransaction;


@Repository
public interface TdsTransactionRepository extends JpaRepository<TdsTransaction, Long> {
}


