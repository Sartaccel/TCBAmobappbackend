package com.example.TCBA.Request;

import lombok.Data;

@Data
public class ContainerCreateRequest {

    private String containerNumber;
    private String containerSize;
    private String customerName;
    private String sealNo;
}

