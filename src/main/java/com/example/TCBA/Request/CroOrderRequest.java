package com.example.TCBA.Request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CroOrderRequest {

    private String loginCode;
    private String entryType;
    private LocalDateTime entryDate;
    private String entryNumber;
    private String yardCode;
    private String yardCompanyName;
    private String chaCode;
    private String chaCompanyName;
    private String linerCode;
    private String lineCompanyName;
    private String transportCode;
    private String transportName;
    private Integer noOfContainer;
    private Integer count20ft;
    private Integer count40ft;
}
