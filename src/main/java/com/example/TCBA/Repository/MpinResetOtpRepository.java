package com.example.TCBA.Repository;

import com.example.TCBA.Entity.MpinResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MpinResetOtpRepository
        extends JpaRepository<MpinResetOtp, Long> {

    Optional<MpinResetOtp>
    findTopByEmailAndUsedFalseOrderByIdDesc(String email);
}
