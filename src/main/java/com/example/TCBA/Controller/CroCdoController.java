package com.example.TCBA.Controller;

import com.example.TCBA.Entity.CroCdoOrder;
import com.example.TCBA.Request.CroCdoRequest;
import com.example.TCBA.Response.ApiResponse;
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
    public ResponseEntity<ApiResponse> createCroCdo(
            @RequestBody CroCdoRequest request) {

        try {
            service.saveOrder(request);

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    "Details added successfully",
                    HttpStatus.OK
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
    }

}
