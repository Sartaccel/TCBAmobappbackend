package com.example.TCBA.Controller;

import com.example.TCBA.Service.RazorpayWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/webhook/razorpay")
@RequiredArgsConstructor
public class RazorpayWebhookController {

    private final RazorpayWebhookService webhookService;

    @PostMapping
    public String handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature,
            HttpServletRequest request
    ) {
        webhookService.processWebhook(payload, signature);
        return "OK";
    }
}
