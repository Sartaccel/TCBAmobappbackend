package com.example.TCBA.Repository;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.CroCdoOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CroCdoOrderRepository extends JpaRepository<CroCdoOrder, Long> {

    @Query("""
SELECT COUNT(c)
FROM CroCdoOrder c
WHERE c.entryNumber LIKE CONCAT(:prefix, '-%')
""")
    long countByPrefix(@Param("prefix") String prefix);
}

