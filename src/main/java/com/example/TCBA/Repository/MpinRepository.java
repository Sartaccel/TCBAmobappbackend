package com.example.TCBA.Repository;

import com.example.TCBA.Entity.Mpin;
import com.example.TCBA.Entity.BrokerLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MpinRepository extends JpaRepository<Mpin, Long> {

    Optional<Mpin> findByBroker(BrokerLogin broker);
    boolean existsByBroker(BrokerLogin broker);

}
