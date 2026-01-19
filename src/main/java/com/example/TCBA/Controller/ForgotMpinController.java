package com.example.TCBA.Controller;

import com.example.TCBA.Request.ForgotMpinRequest;
import com.example.TCBA.Request.ResetMpinRequest;
import com.example.TCBA.Request.VerifyMpinOtpRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Service.ForgotMpinService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mpin")
@RequiredArgsConstructor
public class ForgotMpinController {

    private final ForgotMpinService service;
    private final CommonUtil commonUtil;

    // SEND OTP
    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse> forgotMpin(
            @RequestBody ForgotMpinRequest req) {

        service.sendOtp(req.getEmail());

        return ResponseEntity.ok(
                new ApiResponse(
                        "SUCCESS",
                        commonUtil.getResponseMessage("resp.tcba.mpin.forgot.ok"),
                        HttpStatus.OK,""
                )
        );
    }

    // VERIFY OTP
    @PostMapping("/forgot/verify")
    public ResponseEntity<ApiResponse> verifyOtp(
            @RequestBody VerifyMpinOtpRequest req) {

        service.verifyOtp(req.getEmail(), req.getOtp());

        return ResponseEntity.ok(
                new ApiResponse(
                        "SUCCESS",
                        commonUtil.getResponseMessage("resp.tcba.mpin.otp.verify.ok"),
                        HttpStatus.OK,""
                )
        );
    }

    // RESET MPIN
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetMpin(
            @RequestBody ResetMpinRequest req) {

        service.resetMpin(req.getEmail(), req);

        return ResponseEntity.ok(
                new ApiResponse(
                        "SUCCESS",
                        commonUtil.getResponseMessage("resp.tcba.mpin.reset.ok"),
                        HttpStatus.OK,"MPIN_RESET_OK"
                )
        );
    }

    private String getAuthenticatedEmail() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("Unauthenticated request");
        }

        return auth.getName(); // email from JWT
    }

}

