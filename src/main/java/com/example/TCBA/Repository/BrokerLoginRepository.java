package com.example.TCBA.Repository;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Response.LinerDropdownView;
import com.example.TCBA.Response.YardDropdownView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrokerLoginRepository extends JpaRepository<BrokerLogin, Long> {

    Optional<BrokerLogin> findByEmail(String email);
    Optional<BrokerLogin> findByGst(String gst);
    Optional<BrokerLogin> findByStackHolderId(String stackHolderId);
    List<YardDropdownView> findYardsByStackHoldersType_Id(Long typeId);

    List<LinerDropdownView> findLinersByStackHoldersType_Id(Long typeId);

//    List<BrokerLogin> findByStackHolderType_Id(Long typeId);


}
