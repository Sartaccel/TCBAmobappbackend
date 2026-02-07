package com.example.TCBA.Repository;

import com.example.TCBA.Entity.CroCdoOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CroCdoOrderRepository extends JpaRepository<CroCdoOrder, Long> {

    Optional<CroCdoOrder> findByEntryNumber(String entryNumber);

    Optional<CroCdoOrder> findByEntryNumberAndEntryTypeAndContainerNo(
            String entryNumber,
            String entryType,
            String containerNo
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE CroCdoOrder c
    SET c.approvalStatus = :status
    WHERE c.referenceId IN :referenceIds
""")
    int updateApprovalStatus(String status, List<String> referenceIds);

    Page<CroCdoOrder> findByLoginCode(String stackHolderId, Pageable pageable);

    @Query("""
SELECT c FROM CroCdoOrder c
WHERE c.loginCode = :stackHolderId
AND c.entryDate BETWEEN :startDate AND :endDate
AND (:entryType IS NULL OR c.entryType = :entryType)
AND (:entryNumber IS NULL OR LOWER(c.entryNumber) LIKE LOWER(CONCAT('%', :entryNumber, '%')))
ORDER BY c.entryDate DESC
""")
    List<CroCdoOrder> searchOrders(
            @Param("stackHolderId") String stackHolderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("entryType") String entryType,
            @Param("entryNumber") String entryNumber
    );



    List<CroCdoOrder> findByLoginCodeAndEntryNumber(
            String loginCode,
            String entryNumber
    );

}

