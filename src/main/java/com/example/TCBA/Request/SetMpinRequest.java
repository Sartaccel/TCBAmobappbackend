package com.example.TCBA.Request;

import lombok.Data;

@Data
public class SetMpinRequest {
    private String mpin;
    private String confirmMpin;
}
