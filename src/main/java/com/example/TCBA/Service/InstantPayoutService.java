package com.example.TCBA.Service;

import com.example.TCBA.Entity.Beneficiary;
import com.example.TCBA.Entity.PayoutTransaction;
import com.example.TCBA.Entity.Wallet;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BeneficiaryRepository;
import com.example.TCBA.Repository.PayoutTransactionRepository;
import com.example.TCBA.Repository.WalletRepository;
import com.example.TCBA.Request.InstantPayoutRequest;
import jakarta.transaction.Transactional;
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

@Service
public class InstantPayoutService {

    private final RestTemplate restTemplate;
    private final PayoutTransactionRepository payoutRepo;
    private final BeneficiaryRepository beneficiaryRepo;
    private final WalletRepository walletRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.merchant.account}")
    private String merchantAccount;

    public InstantPayoutService(
            RestTemplate restTemplate,
            PayoutTransactionRepository payoutRepo,
            BeneficiaryRepository beneficiaryRepo,
            WalletRepository walletRepository
    ) {
        this.restTemplate = restTemplate;
        this.payoutRepo = payoutRepo;
        this.beneficiaryRepo = beneficiaryRepo;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public JSONObject instantPayout(InstantPayoutRequest req) {

        Wallet wallet =
                walletRepository.findByStakeHolderId(req.getStackHolderId())
                        .orElseThrow(() ->
                                new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (!wallet.getIsActive()) {
            throw new AppException(ErrorCode.WALLET_BLOCKED);
        }

        BigDecimal withdrawAmount = req.getAmount();
        if (wallet.getBalance().compareTo(withdrawAmount) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        String referenceId =
                "INSTANT_" + req.getStackHolderId() + "_" +
                        System.currentTimeMillis();

        // 1️⃣ create txn first
        PayoutTransaction txn = new PayoutTransaction();
        txn.setStackHolderId(req.getStackHolderId());
        txn.setAmount(req.getAmount());
        txn.setReferenceId(referenceId);
        txn.setUpiId(req.getUpiId());
        txn.setYardName(req.getYardName());
        txn.setStatus(PayoutTransaction.PayoutStatus.CREATED);
        payoutRepo.save(txn);

        try {
            Beneficiary beneficiary =
                    beneficiaryRepo.findByUpiId(req.getUpiId())
                            .orElseGet(() -> createBeneficiary(req));

            BigDecimal amountInPaise =
                    req.getAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(0, RoundingMode.HALF_UP);

            JSONObject payout = new JSONObject();
            payout.put("account_number", merchantAccount);
            payout.put("amount", amountInPaise.intValueExact());
            payout.put("currency", "INR");
            payout.put("purpose", "payout");
            payout.put("reference_id", referenceId);
            payout.put("mode", "UPI");
            payout.put("fund_account_id", beneficiary.getFundAccountId());

            JSONObject razorResp = callRazorpay(payout);

            // 2️⃣ update txn
            txn.setRazorpayPayoutId(razorResp.getString("id"));
            txn.setStatus(PayoutTransaction.PayoutStatus.PROCESSING);
            payoutRepo.save(txn);

            // 3️⃣ debit wallet AFTER success
            wallet.setBalance(wallet.getBalance().subtract(withdrawAmount));
            walletRepository.save(wallet);

            return razorResp;

        } catch (Exception e) {
            txn.setStatus(PayoutTransaction.PayoutStatus.FAILED);
            payoutRepo.save(txn);
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
        fund.put("account_type", "vpa");

        JSONObject vpa = new JSONObject();
        vpa.put("address", req.getUpiId());
        fund.put("vpa", vpa);

        JSONObject fundResp =
                callRazorpay(fund, "https://api.razorpay.com/v1/fund_accounts");

        String fundAccountId = fundResp.getString("id");

        // 🔹 Save Beneficiary
        Beneficiary ben = new Beneficiary();
        ben.setName(req.getAccountHolderName());
        ben.setPhone(req.getPhone());
        ben.setUpiId(req.getUpiId());
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


