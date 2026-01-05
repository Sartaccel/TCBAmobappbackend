package com.example.TCBA.Request;

import lombok.Data;

@Data
public class ChangeMpinRequest {
    private String oldMpin;
    private String newMpin;
    private String confirmMpin;
}
