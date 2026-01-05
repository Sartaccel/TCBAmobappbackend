package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final BrokerLoginRepository brokerLoginRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CommonUtil commonUtil;

    public void sendOtp(String email) {

        BrokerLogin CHA = brokerLoginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        CHA.setResetOtp(otp);
        CHA.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        brokerLoginRepository.save(CHA);
        emailService.sendOtpEmail(email, otp);
    }

    public void verifyOtp(String email, String otp) {

        BrokerLogin CHA = brokerLoginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!otp.equals(CHA.getResetOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (CHA.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
    }

    public void resetPassword(String email, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException(
                    commonUtil.getResponseMessage("resp.tcba.password.mismatch")
            );
        }
        BrokerLogin CHA = brokerLoginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CHA.setPassword(passwordEncoder.encode(newPassword));
        CHA.setResetOtp(null);
        CHA.setOtpExpiry(null);
        brokerLoginRepository.save(CHA);
    }
}
