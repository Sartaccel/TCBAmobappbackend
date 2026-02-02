package com.example.TCBA.Response;

import lombok.Data;

@Data
public class ChaDashboardResponse {

    private String status;
    private DataBlock data;

    @Data
    public static class DataBlock {

        private String dateRange;
        private Containers containers;
        private Approval approval;
        private PaymentRequests paymentRequests;
        private Proforma proforma;
        private Invoice invoice;
    }

    @Data
    public static class Containers {
        private int total;
        private int in;
        private int out;
        private int count20ft;
        private int count40ft;
        private int teus;
    }

    @Data
    public static class Approval {
        private int pending;
        private int accepted;
        private int rejected;
    }

    @Data
    public static class PaymentRequests {
        private int all;
        private int pending;
        private long amountPending;
        private long amountReceived;
    }

    @Data
    public static class Proforma {
        private int all;
        private int raised;
        private int approved;
        private int rejected;
    }

    @Data
    public static class Invoice {
        private int all;
        private int raised;
        private int approved;
        private int rejected;
    }
}

