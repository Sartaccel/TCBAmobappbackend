package com.example.TCBA.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GateContainerSearchRequest {
    private String chaCode;
    private DateFilter filters;   // optional
    private String entryType;
    private Integer page;
    private Integer limit;
}
