package com.example.TCBA.Request;

import lombok.Data;

@Data
public class ResetMpinRequest {
    private String email;
    private String newMpin;
    private String confirmMpin;
}

