package com.example.TCBA.Controller;

import com.example.TCBA.Request.YardPaymentRequest;
import com.example.TCBA.Request.YardPayoutRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.InstantPayoutResponse;
import com.example.TCBA.Service.YardPayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tcba/yard")
@RequiredArgsConstructor
public class YardPayoutController {

    private final YardPayoutService yardPayoutService;

    @PostMapping("/payout")
    public ResponseEntity<?> payout(
            @RequestBody YardPaymentRequest request
    ) {

        YardPayoutRequest payout = new YardPayoutRequest();

        payout.setStackHolderId(request.getStackHolderId());
        payout.setYardGstNumber(request.getYardGstNumber());
        payout.setAmount(request.getAmount());
        payout.setEntryNo(request.getEntryNo());
        payout.setContainerNo(request.getContainerNo());
        payout.setPaymentRequestId(request.getPaymentRequestId());
        payout.setYardId(request.getYardId());
        payout.setContainerSize(request.getContainerSize());
        payout.setGateDateTime(request.getGateDateTime());
        payout.setPaymentMethod(request.getPaymentMethod());
        payout.setPaymentType(request.getPaymentType());

        InstantPayoutResponse payoutResponse =
                yardPayoutService.payoutToYard(payout);

        ApiResponse apiResponse =
                new ApiResponse(
                        "SUCCESS",
                        "Payout processed successfully",
                        HttpStatus.OK,
                        ""
                );

        apiResponse.setData(payoutResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
