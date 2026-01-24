package com.example.TCBA.Response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InstantPayoutResponse {

    private String paymentRequestId;
    private String referenceId;
    private BigDecimal totalAmount;
    private BigDecimal tdsPercent;
    private BigDecimal tdsAmount;
    private BigDecimal withdrawAmount;

    private String status;
}

