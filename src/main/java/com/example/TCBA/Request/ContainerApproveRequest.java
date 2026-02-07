package com.example.TCBA.Request;

import lombok.Data;

import java.util.List;

@Data
public class ContainerApproveRequest {

    private String loginCode;
    private String action;
    private String reason;
    private List<String> entryIds;

}
