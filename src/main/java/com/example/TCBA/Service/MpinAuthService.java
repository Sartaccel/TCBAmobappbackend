package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.Mpin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.MpinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MpinAuthService {

    private final MpinRepository mpinRepo;
    private final PasswordEncoder encoder;

    public void verifyMpin(BrokerLogin user, String inputMpin) {

        Mpin mpin = mpinRepo.findByBroker(user)
                .orElseThrow(() -> new AppException(ErrorCode.MPIN_NOT_SET));

        // 🔒 Already locked
        if (mpin.isLocked()) {
            throw new AppException(ErrorCode.MPIN_LOCKED);
        }

        // ❌ Wrong MPIN
        if (!encoder.matches(inputMpin, mpin.getMpinHash())) {

            int attempts = mpin.getAttempts() + 1;
            mpin.setAttempts(attempts);

            // 🔒 Lock on 3rd attempt
            if (attempts >= 3) {
                mpin.setLocked(true);
                mpinRepo.save(mpin);
                throw new AppException(ErrorCode.MPIN_LOCKED);
            }

            mpinRepo.save(mpin);

            // ⚠️ Last attempt warning
            if (attempts == 2) {
                throw new AppException(ErrorCode.MPIN_LAST_ATTEMPT);
            }

            // ❌ Normal invalid
            throw new AppException(ErrorCode.MPIN_INVALID);
        }

        // ✅ Correct MPIN
        mpin.setAttempts(0);
        mpinRepo.save(mpin);
    }
}


