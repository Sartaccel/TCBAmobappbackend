package com.example.TCBA.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContainerResponse {

    private Long id;
    private String containerNumber;
    private String containerSize;
    private String customerName;
    private String sealNo;
    private String status;
}

