package com.example.TCBA.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoRoEntriesSearchRequest {
    private String loginCode;
    private DateFilter filters;   // optional
    private Integer page;
    private Integer limit;
}
