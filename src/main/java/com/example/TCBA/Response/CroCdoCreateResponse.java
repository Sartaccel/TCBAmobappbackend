package com.example.TCBA.Response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CroCdoCreateResponse {

    private Long orderId;
    private String orderType;
    private String depot;
    private String linerName;
    private String transporterName;
    private String svcType;

    private List<ContainerResponse> containers;
}

