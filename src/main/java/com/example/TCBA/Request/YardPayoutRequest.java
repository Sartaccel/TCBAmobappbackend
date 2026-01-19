package com.example.TCBA.Request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YardPayoutRequest {

    private String fromStackHolderId;
    private String toGstNumber;
    private BigDecimal amount;
}
