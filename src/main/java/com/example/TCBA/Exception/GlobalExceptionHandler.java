package com.example.TCBA.Exception;

import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor

public class GlobalExceptionHandler {

    private final CommonUtil commonUtil;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException ex) {

        HttpStatus status;
        String messageKey;
        String errorCode = "";

        switch (ex.getErrorCode()) {

            // AUTH
            case INVALID_CREDENTIALS -> {
                status = HttpStatus.UNAUTHORIZED;
                messageKey = "resp.tcba.login.invalid";
            }
            case UNAUTHORIZED -> {
                status = HttpStatus.UNAUTHORIZED;
                messageKey = "resp.tcba.unauthorized";
            }
            case FORBIDDEN, ACCOUNT_INACTIVE -> {
                status = HttpStatus.FORBIDDEN;
                messageKey = "resp.tcba.forbidden";
            }

            // MPIN
            case MPIN_INVALID -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.mpin.invalid";
            }
            case MPIN_LAST_ATTEMPT -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.mpin.last_attempt";
            }
            case MPIN_LOCKED -> {
                status = HttpStatus.FORBIDDEN;
                messageKey = "resp.tcba.mpin.locked";
            }
            case MPIN_MISMATCH -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.mpin.mismatch";
            }

            // OTP
            case OTP_INVALID -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.mpin.otp.invalid";
            }
            case OTP_EXPIRED -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.mpin.otp.expired";
            }

            // PASSWORD
            case PASSWORD_MISMATCH -> {
                status = HttpStatus.BAD_REQUEST;
                messageKey = "resp.tcba.password.mismatch";
            }

            // WALLET
            case PAYMENT_REQUIRED, INSUFFICIENT_BALANCE -> {
                status = HttpStatus.PAYMENT_REQUIRED;
                messageKey = "resp.tcba.payment.required";
            }

            // PAYOUT
            case YARD_NOT_FOUND, NOT_A_YARD -> {
                status = HttpStatus.NOT_FOUND;
                messageKey = "resp.tcba.payout.notFountYard";
            }



            default -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                messageKey = "resp.tcba.internal.error";
            }
        }

        return ResponseEntity.status(status)
                .body(new ApiResponse(
                        "FAILURE",
                        commonUtil.getResponseMessage(messageKey),
                        status,""
                ));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleOther(Exception ex) {

        ApiResponse response = new ApiResponse(
                "FAILURE",
                commonUtil.getResponseMessage("resp.tcba.internal.error"),
                HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
