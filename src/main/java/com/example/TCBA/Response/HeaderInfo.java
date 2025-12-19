package com.example.TCBA.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HeaderInfo {
    private String status;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}
