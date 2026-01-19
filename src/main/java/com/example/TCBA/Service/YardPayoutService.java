package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class YardPayoutService {

    private final BrokerLoginRepository brokerRepo;
    private final InstantPayoutService instantPayoutService;

    public YardPayoutService(BrokerLoginRepository brokerRepo, InstantPayoutService instantPayoutService) {
        this.brokerRepo = brokerRepo;
        this.instantPayoutService = instantPayoutService;
    }

    public Map<String, Object> payoutToYard(YardPayoutRequest req) {

        BrokerLogin yard =
                brokerRepo.findByGst(req.getYardGstNumber())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.YARD_NOT_FOUND)
                        );

        if (!yard.getStackHoldersType().getId().equals(3L)) {
            throw new AppException(ErrorCode.NOT_A_YARD);
        }

        InstantPayoutRequest payoutReq = new InstantPayoutRequest();

        payoutReq.setStackHolderId(req.getStackHolderId());
        payoutReq.setAmount(req.getAmount());
        payoutReq.setUpiId(yard.getPrimaryUpiId());
        payoutReq.setAccountHolderName(
                yard.getPrimaryAccountHolderName()
        );
        payoutReq.setPhone(yard.getPhoneNumber());
        payoutReq.setYardName(yard.getLegalName());

        // ✅ call payout (ignore response)
        instantPayoutService.instantPayout(payoutReq);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Payout processed successfully");
        response.put("entryId", req.getEntryNo());
        response.put("amount", req.getAmount());
        response.put("containerNo", req.getContainerNo());

        return response;
    }

}

