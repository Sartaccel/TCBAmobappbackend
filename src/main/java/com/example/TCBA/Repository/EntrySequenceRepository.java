package com.example.TCBA.Repository;

import com.example.TCBA.Entity.EntrySequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntrySequenceRepository
        extends JpaRepository<EntrySequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EntrySequence e WHERE e.prefix = :prefix")
    EntrySequence findForUpdate(@Param("prefix") String prefix);
}


