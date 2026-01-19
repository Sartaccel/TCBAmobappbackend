package com.example.TCBA.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YardPaymentRequest {
    private String stackHolderId;
    private String yardGstNumber;
    private BigDecimal amount;
    private String entryNo;
    private String containerNo;
}
