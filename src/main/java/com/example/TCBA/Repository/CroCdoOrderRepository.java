package com.example.TCBA.Repository;

import com.example.TCBA.Entity.CroCdoOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CroCdoOrderRepository extends JpaRepository<CroCdoOrder, Long> {

    Optional<CroCdoOrder> findByEntryNumber(String entryNumber);

    Optional<CroCdoOrder> findByEntryNumberAndEntryTypeAndContainerNo(
            String entryNumber,
            String entryType,
            String containerNo
    );

    Optional<CroCdoOrder> findByEntryNumberAndContainerNo(String entryNumber, String containerNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE CroCdoOrder c
    SET c.approvalStatus = :status
    WHERE c.referenceId IN :referenceIds
""")
    int updateApprovalStatus(String status, List<String> referenceIds);

    Page<CroCdoOrder> findByLoginCode(String stackHolderId, Pageable pageable);

    Page<CroCdoOrder> findByLoginCodeAndEntryType(
            String stackHolderId,
            String entryType,
            Pageable pageable
    );

}

