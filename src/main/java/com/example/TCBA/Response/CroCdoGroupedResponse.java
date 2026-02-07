package com.example.TCBA.Response;

import com.example.TCBA.Entity.CroCdoOrder;
import lombok.Data;

import java.util.List;

@Data
public class CroCdoGroupedResponse {

    private String loginCode;
    private String entryNumber;
    private List<CroCdoOrder> orders;
}
