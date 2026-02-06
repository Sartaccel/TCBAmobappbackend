package com.example.TCBA.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    public RazorpayService(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret
    ) throws Exception {

        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public Order createOrder(int amount) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount); // paise
        options.put("currency", "INR");
        options.put("payment_capture", 1);   // 🔥 MUST

        return razorpayClient.orders.create(options);
    }
}
