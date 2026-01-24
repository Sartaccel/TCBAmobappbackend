package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Response.InstantPayoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class YardPayoutService {

    private final BrokerLoginRepository brokerRepo;
    private final InstantPayoutService instantPayoutService;

    public InstantPayoutResponse payoutToYard(YardPayoutRequest req)
    {

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
        payoutReq.setAccountNumber(yard.getPrimaryAccountNumber());
        payoutReq.setIfsc(yard.getPrimaryIfsc());
        payoutReq.setPhone(yard.getPhoneNumber());
        payoutReq.setYardName(yard.getLegalName());
        payoutReq.setEntryNo(req.getEntryNo());
        payoutReq.setContainerNo(req.getContainerNo());
        payoutReq.setPaymentRequestId(req.getPaymentRequestId());

        InstantPayoutResponse payoutResponse =
                instantPayoutService.instantPayout(payoutReq);


        Map<String, Object> response = new HashMap<>();

        response.put("status", "SUCCESS");
        response.put("message", "Payout processed successfully");

        response.put("paymentRequestId", payoutResponse.getPaymentRequestId());
        response.put("referenceId", payoutResponse.getReferenceId());

        response.put("totalAmount", payoutResponse.getTotalAmount());
        response.put("tdsPercent", payoutResponse.getTdsPercent());
        response.put("tdsAmount", payoutResponse.getTdsAmount());
        response.put("withdrawAmount", payoutResponse.getWithdrawAmount());

        response.put("containerNo", req.getContainerNo());
        response.put("entryNo", req.getEntryNo());

        return payoutResponse;

    }

}

