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

    // 🔹 CRO-specific counts
    private Integer totalContainers;
    private Integer container20FtCount;
    private Integer container40FtCount;


    private List<ContainerCreateRequest> containers;
}

