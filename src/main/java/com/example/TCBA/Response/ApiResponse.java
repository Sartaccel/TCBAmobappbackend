package com.example.TCBA.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private HeaderInfo headers;
    private Object data;

    public ApiResponse(String status, String message, HttpStatus statusCode,String errorCode) {
        this.headers = new HeaderInfo(status, message, statusCode.value(), LocalDateTime.now());
    }
}
