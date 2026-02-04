package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Entity.PaymentDetails;
import com.example.TCBA.Entity.YardInstantPayoutRequest;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.*;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Response.InstantPayoutResponse;
import com.example.TCBA.Util.AesEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class YardPayoutService {

    private final BrokerLoginRepository brokerRepo;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final InstantPayoutService instantPayoutService;
    private final CroCdoOrderRepository croCdoOrderRepo;
    private final PayoutTransactionRepository payoutRepo;
    private final YardInstantPayoutRequestRepository yardInstantPayoutRequestRepository;
    private final AesEncryptionUtil util;

    public InstantPayoutResponse payoutToYard(YardPayoutRequest req)
    {

        System.out.println(req.getGateDateTime());

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

        CroCdoOrder croCdoOrder =
                croCdoOrderRepo.findByEntryNumber(req.getEntryNo())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.ENTRY_ID_NOT_FOUND));

        if (!yard.getStackHoldersType().getId().equals(3L)) {
            throw new AppException(ErrorCode.NOT_A_YARD);
        }

        if (!paymentDetails.getPaymentTerms().equals("instant"))
        {
            throw new AppException(ErrorCode.NOT_A_INSTANT_USER);
        }

        if(!croCdoOrder.getEntryNumber().equals(req.getEntryNo()))
        {
            throw new AppException(ErrorCode.ENTRY_ID_NOT_FOUND);
        }

        if(!croCdoOrder.getContainerNo().equals(req.getContainerNo()))
        {
            throw new AppException(ErrorCode.CONTAINER_NUMBER_NOT_FOUND);
        }

        if(!croCdoOrder.getLoginCode().equals(req.getStackHolderId()))
        {
            throw new AppException(ErrorCode.CHA_ID_MISMATCH);
        }

        if (req.getGateDateTime().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.GATE_IN_DATE_CANNOT_BE_FUTURE);
        }

        if(!croCdoOrder.getPaymentStatus().equalsIgnoreCase("PENDING"))
        {
            throw new AppException(ErrorCode.ALREADY_PAID);
        }

        if(!croCdoOrder.getYardCode().equals(req.getYardId()))
        {
            throw new AppException(ErrorCode.YARD_ID_MISMATCH);
        }

//        String containerSize = req.getContainerSize(); // "20" or "40"
//        BigDecimal inputAmount = new BigDecimal(req.getAmount());
//
//        String settingKey;
//
//        if ("20".equals(containerSize)) {
//            settingKey = "20FT_AMOUNT";
//        } else if ("40".equals(containerSize)) {
//            settingKey = "40FT_AMOUNT";
//        } else {
//            throw new AppException(ErrorCode.INVALID_CONTAINER_SIZE);
//        }
//
//        BigDecimal dbAmount =
//                new BigDecimal(
//                        adminSettingsRepository
//                                .findBySettingsName(settingKey)
//                                .orElseThrow(() ->
//                                        new AppException(ErrorCode.SETTING_NOT_FOUND)
//                                )
//                                .getSettingsValue()
//                );
//
//        if (inputAmount.compareTo(dbAmount) != 0) {
//            throw new AppException(ErrorCode.INVALID_AMOUNT);
//        }
//    }

        YardInstantPayoutRequest payment = YardInstantPayoutRequest.builder()
                .chaId(req.getStackHolderId())
                .yardId(req.getYardId())
                .yardGstNo(util.encrypt(req.getYardGstNumber()))
                .entryNumber(req.getEntryNo())
                .containerNos(req.getContainerNo())
                .paymentAmount(req.getAmount())
                .paymentRequestId(req.getPaymentRequestId())
                .gateInOutDatetime(req.getGateDateTime())
                .paymentType(req.getPaymentType())
                .paymentMethod(req.getPaymentMethod())
                .verificationStatus("VERIFIED")
                .payoutStatus("PENDING")
                .requestReceivedAt(LocalDateTime.now())
                .build();

        yardInstantPayoutRequestRepository.save(payment);

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

