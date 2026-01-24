package com.example.TCBA.Service;

import com.example.TCBA.Entity.*;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.*;
import com.example.TCBA.Request.InstantPayoutRequest;
import com.example.TCBA.Response.InstantPayoutResponse;
import com.razorpay.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class InstantPayoutService {

    private final RestTemplate restTemplate;
    private final PayoutTransactionRepository payoutRepo;
    private final BeneficiaryRepository beneficiaryRepo;
    private final WalletRepository walletRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final BrokerLoginRepository brokerLoginRepository;
    private final TdsTransactionRepository tdsRepository;


    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.merchant.account}")
    private String merchantAccount;

    @Transactional
    public InstantPayoutResponse instantPayout(InstantPayoutRequest req)
    {

        Wallet wallet =
                walletRepository.findByStakeHolderId(req.getStackHolderId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getIsActive()) {
            throw new AppException(ErrorCode.WALLET_BLOCKED);
        }

        BrokerLogin brokerLogin =
                brokerLoginRepository.findByStackHolderId(req.getStackHolderId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.FORBIDDEN));

        PaymentDetails payment =
                paymentDetailsRepository.findByStackHolders_Id(brokerLogin.getId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.FORBIDDEN));

        BigDecimal amount = req.getAmount();      // 10000

        String tdsPercentStr = payment.getTdsPercent(); // "2"

        BigDecimal tdsPercent =
                new BigDecimal(tdsPercentStr);

        BigDecimal tdsAmount =
                amount
                        .multiply(tdsPercent)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal withdrawAmount =
                amount.subtract(tdsAmount);

        if (wallet.getBalance().compareTo(withdrawAmount) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        String referenceId =
                "INSTANT_" + req.getStackHolderId() + "_" +
                        System.currentTimeMillis();

        // 1️⃣ create txn first
        PayoutTransaction txn = new PayoutTransaction();
        txn.setStackHolderId(req.getStackHolderId());
        txn.setAmount(withdrawAmount);
        txn.setTds_Detected(tdsPercent);
        txn.setTotal_Amount(req.getAmount());
        txn.setReferenceId(referenceId);
        txn.setUpiId(req.getUpiId());
        txn.setBankAccountNumber(req.getAccountNumber());
        txn.setIfscCode(req.getIfsc());
        txn.setPaymentRequestId(req.getPaymentRequestId());
        txn.setYardName(req.getYardName());
        txn.setEntryNo(req.getEntryNo());
        txn.setContainerNo(req.getContainerNo());
        txn.setStatus(PayoutTransaction.PayoutStatus.CREATED);
        payoutRepo.save(txn);

        TdsTransaction tdsTxn = new TdsTransaction();

        tdsTxn.setPaymentRequestId(req.getPaymentRequestId());
        tdsTxn.setPayoutReferenceId(referenceId);
        tdsTxn.setStackHolderId(req.getStackHolderId());

        tdsTxn.setYardName(req.getYardName());
        tdsTxn.setEntryNo(req.getEntryNo());
        tdsTxn.setContainerNo(req.getContainerNo());

        tdsTxn.setTotalAmount(amount);
        tdsTxn.setTdsPercent(tdsPercent);
        tdsTxn.setTdsAmount(tdsAmount);
        tdsTxn.setNetPaidAmount(withdrawAmount);

        tdsTxn.setTdsStatus(TdsTransaction.TdsStatus.UNPAID);
        tdsTxn.setPayoutStatus(PayoutTransaction.PayoutStatus.CREATED);

        tdsRepository.save(tdsTxn);

        try {
            if (
                    (req.getUpiId() == null || req.getUpiId().isBlank()) &&
                            (req.getAccountNumber() == null || req.getIfsc() == null)
            ) {
                throw new RuntimeException("INVALID_BENEFICIARY_DETAILS");
            }

            Beneficiary beneficiary =
                    beneficiaryRepo.findByUpiId(req.getUpiId())
                            .orElseGet(() -> createBeneficiary(req));

            BigDecimal amountInPaise =
                    withdrawAmount
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(0, RoundingMode.HALF_UP);

            JSONObject payout = new JSONObject();
            payout.put("account_number", merchantAccount);
            payout.put("amount", amountInPaise.intValueExact());
            payout.put("currency", "INR");
            payout.put("purpose", "payout");
            payout.put("reference_id", referenceId);
            if (req.getUpiId() != null && !req.getUpiId().isBlank()) {
                payout.put("mode", "UPI");
            } else {
                payout.put("mode", "IMPS");
            }
            payout.put("fund_account_id", beneficiary.getFundAccountId());

            JSONObject razorResp = callRazorpay(payout);

            // 2️⃣ update txn
            txn.setRazorpayPayoutId(razorResp.getString("id"));
            txn.setStatus(PayoutTransaction.PayoutStatus.PROCESSING);
            payoutRepo.save(txn);

            tdsTxn.setRazorpayPayoutId(razorResp.getString("id"));
            tdsTxn.setPayoutStatus(PayoutTransaction.PayoutStatus.PROCESSING);
            tdsRepository.save(tdsTxn);


            // 3️⃣ debit wallet AFTER success
            wallet.setBalance(wallet.getBalance().subtract(withdrawAmount));
            walletRepository.save(wallet);


            InstantPayoutResponse response = new InstantPayoutResponse();

            response.setPaymentRequestId(req.getPaymentRequestId());
            response.setReferenceId(referenceId);
            response.setTotalAmount(amount);
            response.setTdsPercent(tdsPercent);
            response.setTdsAmount(tdsAmount);
            response.setWithdrawAmount(withdrawAmount);

            response.setStatus("PROCESSING");

            return response;

        } catch (Exception e) {
            txn.setStatus(PayoutTransaction.PayoutStatus.FAILED);
            payoutRepo.save(txn);
            tdsTxn.setPayoutStatus(PayoutTransaction.PayoutStatus.FAILED);
            tdsRepository.save(tdsTxn);
            throw e;
        }
    }
    // ================= HELPER METHODS =================

    private Beneficiary createBeneficiary(InstantPayoutRequest req) {

        // 🔹 Create Contact
        JSONObject contact = new JSONObject();
        contact.put("name", req.getAccountHolderName());
        contact.put("contact", req.getPhone());
        contact.put("type", "employee");

        JSONObject contactResp =
                callRazorpay(contact, "https://api.razorpay.com/v1/contacts");

        String contactId = contactResp.getString("id");

        // 🔹 Create Fund Account
        JSONObject fund = new JSONObject();
        fund.put("contact_id", contactId);

        // ===============================
        // ✅ CASE 1 — UPI payout
        // ===============================
        if (req.getUpiId() != null && !req.getUpiId().isBlank()) {

            fund.put("account_type", "vpa");

            JSONObject vpa = new JSONObject();
            vpa.put("address", req.getUpiId());
            fund.put("vpa", vpa);
        }

        // ===============================
        // ✅ CASE 2 — Bank payout
        // ===============================
        else {

            fund.put("account_type", "bank_account");

            JSONObject bank = new JSONObject();
            bank.put("name", req.getAccountHolderName());
            bank.put("ifsc", req.getIfsc());
            bank.put("account_number", req.getAccountNumber());

            fund.put("bank_account", bank);
        }

        JSONObject fundResp =
                callRazorpay(fund, "https://api.razorpay.com/v1/fund_accounts");

        String fundAccountId = fundResp.getString("id");

        Beneficiary ben = new Beneficiary();
        ben.setName(req.getAccountHolderName());
        ben.setPhone(req.getPhone());
        ben.setUpiId(req.getUpiId()); // nullable
        ben.setContactId(contactId);
        ben.setFundAccountId(fundAccountId);

        return beneficiaryRepo.save(ben);
    }

    private JSONObject callRazorpay(JSONObject body) {
        return callRazorpay(body, "https://api.razorpay.com/v1/payouts");
    }

    private JSONObject callRazorpay(JSONObject body, String url) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(keyId, keySecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity =
                new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, entity, String.class);

        return new JSONObject(response.getBody());
    }
}


