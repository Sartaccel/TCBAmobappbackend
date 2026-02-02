package com.example.TCBA.Request;

import lombok.Data;

@Data
public class ClientTokenRequest {

    private String clientId;
    private String clientSecret;
}

