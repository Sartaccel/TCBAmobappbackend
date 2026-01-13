package com.example.TCBA.Service;

import com.example.TCBA.Config.RazorpayConfig;
import com.example.TCBA.Entity.*;
import com.example.TCBA.Repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
        System.out.println("Creating Razorpay order for ₹" + amount);
        String email = jwtUtil.getLoggedInEmail(request);

        BrokerLogin broker = brokerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Broker not found"));

        Wallet wallet = walletRepo.findByBrokerLogin(broker)
                .orElseGet(() -> walletRepo.save(
                        Wallet.builder()
                                .walletId(UUID.randomUUID().toString())
                                .brokerLogin(broker)
                                .stakeHolder(broker)
                                .balance(BigDecimal.ZERO)
                                .isActive(true)
                                .build()
                ));
        if (wallet.getStakeHolder() == null) {
            wallet.setStakeHolder(broker);
        }
        var order = razorpayService.createOrder(amount);

        WalletTransaction txn = WalletTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .wallet(wallet)
                .stakeHolder(broker)
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

    @Transactional
    public void verifyAndCredit(String orderId, String paymentId, String signature) {
        WalletTransaction txn = txnRepo.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (txn.getStatus() == WalletTransaction.TxnStatus.SUCCESS) {
            return;
        }
        Wallet wallet = txn.getWallet();
        // 🔥 ABSOLUTE GUARANTEE (THIS FIXES THE ERROR)
        if (wallet.getStakeHolder() == null) {
            wallet.setStakeHolder(wallet.getBrokerLogin());
        }
        wallet.setBalance(wallet.getBalance().add(txn.getAmount()));
        walletRepo.save(wallet);
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
