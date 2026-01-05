package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.Mpin;
import com.example.TCBA.Entity.MpinResetOtp;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.MpinRepository;
import com.example.TCBA.Repository.MpinResetOtpRepository;
import com.example.TCBA.Request.ResetMpinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgotMpinService {

    private final BrokerLoginRepository brokerRepo;
    private final MpinRepository mpinRepo;
    private final MpinResetOtpRepository otpRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    // 1️⃣ SEND OTP
    public void sendOtp(String email) {

        brokerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        MpinResetOtp resetOtp = new MpinResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtp(otp);
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        resetOtp.setUsed(false);

        otpRepo.save(resetOtp);

        emailService.sendEmail(
                email,
                "Forgot MPIN - OTP",
                "Your OTP is: " + otp + " (valid for 10 minutes)"
        );
    }

    // 2️⃣ VERIFY OTP
    public void verifyOtp(String email, String otp) {

        MpinResetOtp resetOtp =
                otpRepo.findTopByEmailAndUsedFalseOrderByIdDesc(email)
                        .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (resetOtp.isUsed()
                || resetOtp.getExpiryTime().isBefore(LocalDateTime.now())
                || !resetOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        resetOtp.setUsed(true);
        otpRepo.save(resetOtp);
    }

    // 3️⃣ RESET MPIN
    public void resetMpin(String email, ResetMpinRequest req) {

        if (!req.getNewMpin().equals(req.getConfirmMpin()))
            throw new RuntimeException("MPIN mismatch");

        BrokerLogin user =
                brokerRepo.findByEmail(email).orElseThrow();

        Mpin mpin =
                mpinRepo.findByBroker(user).orElseThrow();

        mpin.setMpinHash(encoder.encode(req.getNewMpin()));
        mpin.setAttempts(0);
        mpin.setLocked(false);

        mpinRepo.save(mpin);
    }
}
