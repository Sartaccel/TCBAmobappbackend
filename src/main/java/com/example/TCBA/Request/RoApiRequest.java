package com.example.TCBA.Request;

import lombok.Data;

@Data
public class RoApiRequest {
    private String loginCode;

    private String entryType;
    private String entryNumber;
    private String entryDate;

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
