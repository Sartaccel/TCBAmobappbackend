package com.example.TCBA.Repository;

import com.example.TCBA.Entity.BrokerLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrokerLoginRepository extends JpaRepository<BrokerLogin, Long> {

    Optional<BrokerLogin> findByEmail(String email);
}
