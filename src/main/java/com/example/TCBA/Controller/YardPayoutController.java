package com.example.TCBA.Controller;

import com.example.TCBA.Request.YardPaymentRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.InstantPayoutResponse;
import com.example.TCBA.Service.JwtService;
import com.example.TCBA.Service.YardPayoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tcba/yard")
@RequiredArgsConstructor
public class YardPayoutController {

    private final JwtService jwtService;
    private final YardPayoutService yardPayoutService;

    @PostMapping("/payout")
    public ResponseEntity<?> payout(
            @RequestBody YardPaymentRequest request,
            HttpServletRequest httpRequest
    ) {

        // 🔐 extract token
        String token = httpRequest
                .getHeader("Authorization")
                .substring(7);

        // 🔥 VERY IMPORTANT
        String yardId = jwtService.extractYardId(token);

        if (yardId == null) {
            return ResponseEntity
                    .status(403)
                    .body("Only yard partner can access payout API");
        }

        // map request
        YardPayoutRequest payout = new YardPayoutRequest();
        payout.setStackHolderId(request.getStackHolderId());
        payout.setYardGstNumber(request.getYardGstNumber());
        payout.setAmount(request.getAmount());
        payout.setEntryNo(request.getEntryNo());
        payout.setContainerNo(request.getContainerNo());
        payout.setPaymentRequestId(request.getPaymentRequestId());

        // 🔥 INTERNAL PAYOUT TRIGGER
        InstantPayoutResponse payoutResponse =
                yardPayoutService.payoutToYard(payout);

        ApiResponse apiResponse =
                new ApiResponse(
                        "SUCCESS",
                        "Yard payout processed successfully",
                        HttpStatus.OK,
                        "YARD_PAYOUT_OK"
                );

        apiResponse.setData(payoutResponse);
        return ResponseEntity.ok(apiResponse);

    }
}

