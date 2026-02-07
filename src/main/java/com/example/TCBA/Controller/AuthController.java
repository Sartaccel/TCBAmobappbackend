package com.example.TCBA.Controller;
import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.*;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Service.BrokerLoginService;
import com.example.TCBA.Service.ForgotPasswordService;
import com.example.TCBA.Service.JwtService;
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
    private final JwtService jwtService;
    private final BrokerLoginRepository brokerLoginRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        LoginResponse loginResponse =
                service.login(request.getEmail(), request.getPassword());

        ApiResponse response = new ApiResponse(
                "SUCCESS",
                commonUtil.getResponseMessage("resp.tcba.login.ok"),
                HttpStatus.OK,
                "LOGIN_SUCCESS"
        );

        response.setData(loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        try {
            String email = jwtService.extractUsername(request.getRefreshToken());

            if (jwtService.isTokenExpired(request.getRefreshToken())) {
                throw new RuntimeException("Refresh token expired");
            }

            BrokerLogin broker = brokerLoginRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

            String newAccessToken = jwtService.generateAccessToken(
                    broker.getEmail(),
                    broker.getStackHolderId(),
                    broker.getLegalName()
            );

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    "Access token refreshed successfully",
                    HttpStatus.OK,
                    "SUCCESS"
            );

            response.setData(
                    new LoginResponse(
                            newAccessToken,
                            request.getRefreshToken(), // same refresh token
                            true,// or false if you want to recalc
                            ""
                    )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    "Invalid refresh token",
                    HttpStatus.UNAUTHORIZED,"REFRESH_EXPIRED"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
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
                    HttpStatus.OK,"OTP_SENT"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST,"OTP_SENT_FAILED"
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
                    HttpStatus.OK,"OTP_VERIFIED"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED,"OTP_NOT_VERIFIED"
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
                    HttpStatus.OK,"PASSWORD_RESET_OK"
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST,"PASSWORD_RESET_FAIL"
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

}
