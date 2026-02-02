package com.example.TCBA.Repository;

import com.example.TCBA.Entity.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportRepository extends JpaRepository<Transport,Long> {
    List<Transport> findByIsActiveTrueOrderByTransportNameAsc();
}
