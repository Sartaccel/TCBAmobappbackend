package com.example.TCBA.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private boolean hasMpin;
    private String stakeHolderId;
}
