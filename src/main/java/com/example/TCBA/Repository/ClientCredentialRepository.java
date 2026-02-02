package com.example.TCBA.Repository;

import com.example.TCBA.Entity.ClientCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientCredentialRepository
        extends JpaRepository<ClientCredential, Long> {

    Optional<ClientCredential>
    findByClientIdAndActiveTrue(String clientId);

}

