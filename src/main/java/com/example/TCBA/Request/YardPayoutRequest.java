package com.example.TCBA.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class YardPayoutRequest {

    private String stackHolderId;
    private String yardGstNumber;
    private BigDecimal amount;
    private String entryNo;
    private String containerNo;
    private String paymentRequestId;
    private String yardId;
    private LocalDateTime gateDateTime;
    private String containerSize;
    private String paymentType;
    private String paymentMethod;

}
