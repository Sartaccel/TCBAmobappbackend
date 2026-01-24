package com.example.TCBA.Response;

import lombok.Data;

@Data
public class YardDropdownResponse {
    private String stackHolderId;
    private String legalName;

    public YardDropdownResponse(String stackHolderId, String legalName) {
        this.stackHolderId = stackHolderId;
        this.legalName = legalName;
    }
}
