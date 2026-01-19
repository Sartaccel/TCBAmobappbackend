package com.example.TCBA.Controller;

import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Service.YardPayoutService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payout")
@RequiredArgsConstructor
public class PayoutController {

    private final YardPayoutService yardPayoutService;

    @PostMapping("/yard")
    public ResponseEntity<?> yardPayout(
            @RequestBody YardPayoutRequest request) {

        JSONObject response =
                (JSONObject) yardPayoutService.payoutToYard(request);

        return ResponseEntity.ok(response.toMap());
    }
}
