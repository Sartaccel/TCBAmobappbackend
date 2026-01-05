package com.example.TCBA.Service;

import com.example.TCBA.Entity.Wallet;
import com.example.TCBA.Entity.WalletTransaction;
import com.example.TCBA.Repository.WalletRepository;
import com.example.TCBA.Repository.WalletTransactionRepository;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RazorpayWebhookService {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private final WalletTransactionRepository txnRepo;
    private final WalletRepository walletRepo;

    public void processWebhook(String payload, String signature) {

        // 🔐 STEP 1: VERIFY SIGNATURE
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Razorpay webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        // 🔥 HANDLE ONLY SUCCESS EVENTS
        if (!eventType.equals("payment.captured")) {
            return; // ignore others
        }

        JSONObject payment = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = payment.getString("order_id");
        String razorpayPaymentId = payment.getString("id");
        BigDecimal amount = payment.getBigDecimal("amount").divide(BigDecimal.valueOf(100));

        // 🔁 STEP 2: FIND TRANSACTION BY ORDER ID
        WalletTransaction txn = txnRepo.findAll().stream()
                .filter(t -> razorpayOrderId.equals(t.getRazorpayOrderId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // 🛑 STEP 3: DUPLICATE CHECK
        if (txn.getStatus() == WalletTransaction.TxnStatus.SUCCESS) {
            return; // already processed
        }

        // 🔥 STEP 4: CREDIT WALLET
        Wallet wallet = txn.getWallet();
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepo.save(wallet);

        // 🔄 STEP 5: UPDATE TRANSACTION
        txn.setRazorpayPaymentId(razorpayPaymentId);
        txn.setStatus(WalletTransaction.TxnStatus.SUCCESS);
        txnRepo.save(txn);
    }
}
