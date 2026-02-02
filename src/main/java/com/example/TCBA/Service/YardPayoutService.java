package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.PaymentDetails;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.PaymentDetailsRepository;
import com.example.TCBA.Repository.PayoutTransactionRepository;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Response.InstantPayoutResponse;
import com.example.TCBA.Util.AesEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class YardPayoutService {

    private final BrokerLoginRepository brokerRepo;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final InstantPayoutService instantPayoutService;
    private final PayoutTransactionRepository payoutRepo;
    private final AesEncryptionUtil util;

    public InstantPayoutResponse payoutToYard(YardPayoutRequest req)
    {

        if (payoutRepo.existsByPaymentRequestId(req.getPaymentRequestId())) {
            throw new AppException(ErrorCode.REQUEST_ID_EXISTS);
        }

        BrokerLogin yard =
                brokerRepo.findByGst((util.encrypt(req.getYardGstNumber())))
                        .orElseThrow(() ->
                                new AppException(ErrorCode.YARD_NOT_FOUND)
                        );

        BrokerLogin CHA =
                brokerRepo.findByStackHolderId(req.getStackHolderId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.USER_NOT_FOUND)
                        );

        PaymentDetails paymentDetails =
                paymentDetailsRepository.findByStackHolders_Id(CHA.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Details Not Found")
                        );

        if (!yard.getStackHoldersType().getId().equals(3L)) {
            throw new AppException(ErrorCode.NOT_A_YARD);
        }

        if (!paymentDetails.getPaymentTerms().equals("instant"))
        {
            throw new AppException(ErrorCode.NOT_A_INSTANT_USER);
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

        response.put("requestId",req.getPaymentRequestId());
        response.put("containerNo", req.getContainerNo());
        response.put("entryNo", req.getEntryNo());

        return payoutResponse;

    }

}

