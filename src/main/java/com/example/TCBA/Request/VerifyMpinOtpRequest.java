package com.example.TCBA.Request;

import lombok.Data;

@Data
public class VerifyMpinOtpRequest {
    private String email;
    private String otp;
}

