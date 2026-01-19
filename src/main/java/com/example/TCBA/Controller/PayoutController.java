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

    /**
     * GST-based Yard Payout
     *
     * Frontend sends:
     * {
     *   "fromStackHolderId": "TCBA001",
     *   "toGstNumber": "TCBACVZ34F1Z5",
     *   "amount": 100
     * }
     */
    @PostMapping("/yard")
    public ResponseEntity<?> yardPayout(
            @RequestBody YardPayoutRequest request) {

        JSONObject response =
                yardPayoutService.payoutToYard(request);

        return ResponseEntity.ok(response.toMap());
    }
}
