
package com.example.TCBA.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChaDashboardRequest {

    @JsonProperty("loginCode")
    private String loginCode;

    @JsonProperty("filters")
    private Filters filters = new Filters();

    @Data
    public static class Filters {
        @JsonProperty("dateRange")
        private String dateRange;

        @JsonProperty("fromDate")
        private String fromDate;

        @JsonProperty("toDate")
        private String toDate;
    }
}
