package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.Mpin;
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

    public void setMpin(String email, SetMpinRequest req) {

        BrokerLogin user = brokerRepo.findByEmail(email).orElseThrow();

        if (!req.getMpin().equals(req.getConfirmMpin()))
            throw new RuntimeException("MPIN mismatch");

        Mpin mpin = new Mpin();
        mpin.setBroker(user);
        mpin.setMpinHash(encoder.encode(req.getMpin()));
        mpin.setAttempts(0);
        mpin.setLocked(false);

        mpinRepo.save(mpin);
    }

    public void verifyMpin(String email, String mpinValue) {

        BrokerLogin user = brokerRepo.findByEmail(email).orElseThrow();
        Mpin mpin = mpinRepo.findByBroker(user).orElseThrow();

        if (mpin.isLocked())
            throw new RuntimeException("MPIN locked");

        if (!encoder.matches(mpinValue, mpin.getMpinHash())) {
            mpin.setAttempts(mpin.getAttempts() + 1);
            if (mpin.getAttempts() >= 3) mpin.setLocked(true);
            mpinRepo.save(mpin);
            throw new RuntimeException("Invalid MPIN");
        }

        mpin.setAttempts(0);
        mpinRepo.save(mpin);
    }

    public void changeMpin(String email, ChangeMpinRequest req) {
        verifyMpin(email, req.getOldMpin());

        BrokerLogin user = brokerRepo.findByEmail(email).orElseThrow();
        Mpin mpin = mpinRepo.findByBroker(user).orElseThrow();

        mpin.setMpinHash(encoder.encode(req.getNewMpin()));
        mpin.setAttempts(0);
        mpin.setLocked(false);
        mpinRepo.save(mpin);
    }
}

