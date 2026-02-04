package com.example.TCBA.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CarryzenPaymentUpdateRequest {

    private String requestId;
    private List<ContainerPayment> containers;

    @Data
    public static class ContainerPayment {
        private String _id;
        private String paymentStatus;
        private BigDecimal paidAmount;
        private BigDecimal tdsPercent;
        private BigDecimal tdsAmount;
    }
}
