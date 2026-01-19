package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class YardPayoutService {

    private final BrokerLoginRepository brokerRepo;
    private final InstantPayoutService instantPayoutService;

    public YardPayoutService(BrokerLoginRepository brokerRepo, InstantPayoutService instantPayoutService) {
        this.brokerRepo = brokerRepo;
        this.instantPayoutService = instantPayoutService;
    }

    public JSONObject payoutToYard(YardPayoutRequest req) {

        // 1️⃣ find yard using GST
        BrokerLogin yard =
                brokerRepo.findByGst(req.getToGstNumber())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.YARD_NOT_FOUND)
                        );

        // 2️⃣ validate yard type = 3
        if (!yard.getStackHoldersType().getId().equals(3L)) {
            throw new AppException(ErrorCode.NOT_A_YARD);
        }

        // 4️⃣ build internal payout request
        InstantPayoutRequest payoutReq = new InstantPayoutRequest();

        payoutReq.setStackHolderId(req.getFromStackHolderId());
        payoutReq.setAmount(req.getAmount());
        payoutReq.setUpiId(yard.getPrimaryUpiId());
        payoutReq.setAccountHolderName(
                yard.getPrimaryAccountHolderName()
        );
        payoutReq.setPhone(yard.getPhoneNumber());
        payoutReq.setYardName(yard.getLegalName());

        // 5️⃣ call existing payout logic
        return instantPayoutService.instantPayout(payoutReq);
    }
}

