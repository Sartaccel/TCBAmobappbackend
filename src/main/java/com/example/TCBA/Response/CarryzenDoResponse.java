package com.example.TCBA.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CarryzenDoResponse {

    private String status;
    private String message;
    private List<Data> data;

    @lombok.Data
    public static class Data {

        @JsonProperty("_id")
        private String referenceId;
        private String duplicateKey;
        private String approvalStatus;
        private String entryNumber;
        private String entryType;
        private String containerNo;
    }
}
