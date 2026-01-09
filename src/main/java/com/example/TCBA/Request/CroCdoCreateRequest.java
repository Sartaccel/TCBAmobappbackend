package com.example.TCBA.Request;

import lombok.Data;

import java.util.List;

@Data
public class CroCdoCreateRequest {

    private String orderType;
    private String depot;
    private String linerName;
    private String transporterName;
    private String svcType;

    private List<ContainerCreateRequest> containers;
}

