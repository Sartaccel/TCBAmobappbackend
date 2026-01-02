package com.example.TCBA.Service;

import com.example.TCBA.Config.RazorpayConfig;
import com.example.TCBA.Entity.*;
import com.example.TCBA.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txnRepo;
    private final BrokerLoginRepository brokerRepo;
    private final RazorpayService razorpayService;
    private final com.example.TCBA.Util.JwtUtil jwtUtil;
    private final RazorpayConfig razorpayConfig;


    public Map<String, Object> createTopupOrder(int amount, HttpServletRequest request) throws Exception {
        System.out.println("🟢 Creating Razorpay order for ₹" + amount);
        String email = jwtUtil.getLoggedInEmail(request);

        BrokerLogin broker = brokerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        Wallet wallet = walletRepo.findByBrokerLogin(broker)
                .orElseGet(() -> walletRepo.save(
                        Wallet.builder()
                                .walletId(UUID.randomUUID().toString())
                                .brokerLogin(broker)
                                .balance(BigDecimal.ZERO)
                                .isActive(true)
                                .build()
                ));

        var order = razorpayService.createOrder(amount);

        WalletTransaction txn = WalletTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .wallet(wallet)
                .razorpayOrderId(order.get("id"))
                .amount(BigDecimal.valueOf(amount))
                .txnType(WalletTransaction.TxnType.CREDIT)
                .status(WalletTransaction.TxnStatus.CREATED)
                .build();

        txnRepo.save(txn);

        // 🔥 SEND KEY FROM BACKEND
        Map<String, Object> res = new HashMap<>();
        res.put("orderId", order.get("id"));
        res.put("amount", order.get("amount"));
        res.put("currency", order.get("currency"));
        res.put("razorpayKey", razorpayConfig.getKeyId());

        return res;


    }

    public void verifyAndCredit(
            String orderId,
            String paymentId,
            String signature,
            HttpServletRequest request
    ) {

        if (orderId == null || paymentId == null || signature == null) {
            throw new RuntimeException(
                    "Invalid Razorpay callback data: " +
                            "orderId=" + orderId +
                            ", paymentId=" + paymentId +
                            ", signature=" + signature
            );
        }

        String email = jwtUtil.getLoggedInEmail(request);

        BrokerLogin broker = brokerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        Wallet wallet = walletRepo.findByBrokerLogin(broker)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // ✅ 2. Fetch transaction safely
        WalletTransaction txn = txnRepo.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for orderId"));

        // ✅ 3. Idempotency check
        if (txn.getStatus() == WalletTransaction.TxnStatus.SUCCESS) {
            return; // already credited, do nothing
        }

        if (txnRepo.existsByRazorpayPaymentId(paymentId)) {
            throw new RuntimeException("Duplicate payment detected");
        }

        // ✅ 4. Credit wallet
        wallet.setBalance(wallet.getBalance().add(txn.getAmount()));
        walletRepo.save(wallet);

        // ✅ 5. Update transaction
        txn.setRazorpayPaymentId(paymentId);
        txn.setRazorpaySignature(signature);
        txn.setStatus(WalletTransaction.TxnStatus.SUCCESS);
        txnRepo.save(txn);
    }

    public BigDecimal getBalance(HttpServletRequest request) {
        String email = jwtUtil.getLoggedInEmail(request);
        BrokerLogin broker = brokerRepo.findByEmail(email).orElseThrow();
        return walletRepo.findByBrokerLogin(broker).orElseThrow().getBalance();
    }
}
