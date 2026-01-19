package com.example.TCBA.Controller;

import com.example.TCBA.Request.CroCdoCreateRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.CroCdoCreateResponse;
import com.example.TCBA.Service.CroCdoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cro-cdo")
@RequiredArgsConstructor
public class CroCdoController {

    private final CroCdoService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> create(
            @RequestBody CroCdoCreateRequest request) {

        CroCdoCreateResponse response =
                service.createOrder(request);

        String successMessage =
                "CRO".equalsIgnoreCase(response.getOrderType())
                        ? "CRO order created successfully"
                        : "CDO order created successfully";

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(
                        "SUCCESS",
                        successMessage,
                        HttpStatus.CREATED,""
                )
        );
    }
}
