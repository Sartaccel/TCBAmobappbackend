package com.example.TCBA.Repository;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.StackHolderContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StackHolderContactRepository
        extends JpaRepository<StackHolderContact, Long> {

    Optional<StackHolderContact> findTopByStackHolders(BrokerLogin broker);
}
