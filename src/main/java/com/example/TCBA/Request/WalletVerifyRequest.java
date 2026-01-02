package com.example.TCBA.Request;

import lombok.Data;

@Data
public class WalletVerifyRequest {
    private String orderId;
    private String paymentId;
    private String signature;
}

