package com.example.TCBA.Request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CroCdoOrderRequest {

    private String loginCode;
    private String entryType;
    private LocalDateTime entryDate;
    private String entryNumber;
    private String containerNo;
    private String containerSize;
    private String sealNo;
    private String movementType;
    private String yardCode;
    private String chaCode;
    private String linerCode;
    private String yardCompanyName;
    private String chaCompanyName;
    private String lineCompanyName;
    private String transportName;
    private String transportCode;
    private Integer totalContainers;
    private Integer count20ft;
    private Integer count40ft;
}
