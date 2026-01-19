package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.Mpin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.MpinRepository;
import com.example.TCBA.Request.ChangeMpinRequest;
import com.example.TCBA.Request.SetMpinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MpinService {

    private final BrokerLoginRepository brokerRepo;
    private final MpinRepository mpinRepo;
    private final PasswordEncoder encoder;
    private final MpinAuthService mpinAuthService;

    public void setMpin(String email, SetMpinRequest req) {

        BrokerLogin user = brokerRepo.findByEmail(email).orElseThrow();

        if (!req.getMpin().equals(req.getConfirmMpin()))
            throw new AppException(ErrorCode.INVALID_MPIN);

        Mpin mpin = new Mpin();
        mpin.setBroker(user);
        mpin.setMpinHash(encoder.encode(req.getMpin()));
        mpin.setAttempts(0);
        mpin.setLocked(false);

        mpinRepo.save(mpin);
    }

    public void verifyMpin(String email, String mpinValue) {

        BrokerLogin user = brokerRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        // 🔥 Delegate completely
        mpinAuthService.verifyMpin(user, mpinValue);
    }


//    public void verifyMpin(String email, String mpinValue) {
//
//        BrokerLogin user = brokerRepo.findByEmail(email).orElseThrow();
//        Mpin mpin = mpinRepo.findByBroker(user).orElseThrow();
//
//        if (mpin.isLocked())
//            throw new RuntimeException("MPIN locked");
//
//        if (!encoder.matches(mpinValue, mpin.getMpinHash())) {
//            mpin.setAttempts(mpin.getAttempts() + 1);
//            if (mpin.getAttempts() >= 3) mpin.setLocked(true);
//            mpinRepo.save(mpin);
//            throw new RuntimeException("Invalid MPIN");
//     }
//
//        mpin.setAttempts(0);
//        mpinRepo.save(mpin);
//    }

public void changeMpin(String email, ChangeMpinRequest req) {

    // Step 1: Validate new & confirm MPIN
    if (!req.getNewMpin().equals(req.getConfirmMpin())) {
        throw new AppException(ErrorCode.MPIN_MISMATCH);
    }

    BrokerLogin user = brokerRepo.findByEmail(email)
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

    //Step 2: Verify OLD MPIN (this handles attempts & lock)
    mpinAuthService.verifyMpin(user, req.getOldMpin());

    Mpin mpin = mpinRepo.findByBroker(user)
            .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));

    //Optional (recommended): New MPIN should not be same as old
    if (encoder.matches(req.getNewMpin(), mpin.getMpinHash())) {
        throw new AppException(ErrorCode.MPIN_SAME_AS_OLD);
    }

    // ✅ Step 3: Update MPIN
    mpin.setMpinHash(encoder.encode(req.getNewMpin()));
    mpin.setAttempts(0);
    mpin.setLocked(false);

    mpinRepo.save(mpin);
}
}

