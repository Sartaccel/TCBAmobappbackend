package com.example.TCBA.Request;

import lombok.Data;

@Data
public class CroCdoRequest {

    private String orderType;

    private String depot;
    private String linerName;

    // CRO
    private Integer noOfContainer;
    private Integer container20ft;
    private Integer container40ft;

    // CDO
    private String containerNumber;
    private String containerSize;
    private String customerName;
    private String sealNo;
    private String svcType;

    private String transporterName;
}
