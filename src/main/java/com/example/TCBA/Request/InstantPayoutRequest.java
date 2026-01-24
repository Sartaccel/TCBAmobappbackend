package com.example.TCBA.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InstantPayoutRequest {

    private BigDecimal amount;

    // UPI payout
    private String upiId;

    // Bank payout
    private String bankAccountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String phone;
    private String yardName;
    private String stackHolderId;
    private String entryNo;
    private String containerNo;
    private String paymentRequestId;


    // bank details (optional)
    private String accountNumber;
    private String ifsc;

    private PayoutMode payoutMode;

    public enum PayoutMode {
        UPI,
        IMPS,
        NEFT,
        RTGS
    }
}
