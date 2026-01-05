package com.example.TCBA.Controller;

import com.example.TCBA.Request.ForgotPasswordRequest;
import com.example.TCBA.Request.LoginRequest;
import com.example.TCBA.Request.ResetPasswordRequest;
import com.example.TCBA.Request.VerifyOtpRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Service.BrokerLoginService;
import com.example.TCBA.Service.ForgotPasswordService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BrokerLoginService service;
    private final ForgotPasswordService forgotPasswordService;
    private final CommonUtil commonUtil;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        try {
            LoginResponse loginResponse =
                    service.login(request.getEmail(), request.getPassword());

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.login.ok"),
                    HttpStatus.OK
            );

            response.setData(loginResponse);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    commonUtil.getResponseMessage("resp.tcba.login.fail"),
                    HttpStatus.UNAUTHORIZED
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        try {
            forgotPasswordService.sendOtp(request.getEmail());

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.mpin.forgot.ok"),
                    HttpStatus.OK
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        try {
            forgotPasswordService.verifyOtp(
                    request.getEmail(),
                    request.getOtp()
            );

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.mpin.otp.verify.ok"),
                    HttpStatus.OK
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {
            forgotPasswordService.resetPassword(
                    request.getEmail(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.password.reset.ok"),
                    HttpStatus.OK
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }


}
