package com.example.TCBA.Controller;

import com.example.TCBA.Request.CreateOrderRequest;
import com.example.TCBA.Request.WalletVerifyRequest;
import com.example.TCBA.Service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

//    @PostMapping("/orders")
//    public Object createOrder(
//            @RequestParam int amount,
//            HttpServletRequest request
//    ) throws Exception {
//        return walletService.createTopupOrder(amount, request);
//    }

    @PostMapping("/orders")
    public Object createOrder(
            @RequestBody CreateOrderRequest requestBody,
            HttpServletRequest request
    ) throws Exception {

        int amount = requestBody.getAmount();
        return walletService.createTopupOrder(amount, request);
    }


    @PostMapping("/orders/verify")
    public void verifyPayment(
            @RequestBody WalletVerifyRequest req,
            HttpServletRequest request
    ) {
        walletService.verifyAndCredit(
                req.getOrderId(),
                req.getPaymentId(),
                req.getSignature(),
                request
        );
    }

    @GetMapping("/balance")
    public Object getBalance(HttpServletRequest request) {
        return walletService.getBalance(request);
    }
}
