package com.example.TCBA.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StackHolderProfileResponse {

    private String stakeHolderId;
    private String firstName;
    private String lastName;
    private String legalName;
    private String entityType;
    private LocalDate incorporationDate;
    private String gst;
    private String pan;
    private String license;

    private String email;
    private String phoneNumber;
    private String alternatePhoneNumber;

    // ================= CONTACT DETAILS =================
    private String contactEmail;
    private String contactPhone;
    private String designation;

    // ================= PAYMENT SETTINGS =================
    private String paymentTerms;
    private Boolean invoiceApprovalRequired;
    private String minimumBalance;
    private String tdsPercent;
    private String transactionLimit;
    private String proformaInvoice;
}
