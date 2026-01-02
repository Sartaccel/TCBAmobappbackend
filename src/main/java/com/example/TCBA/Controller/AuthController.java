package com.example.TCBA.Controller;

import com.example.TCBA.Request.LoginRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Service.BrokerLoginService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BrokerLoginService service;
    private final CommonUtil commonUtil;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        try {
            LoginResponse loginResponse =
                    service.login(request.getEmail(), request.getPassword());

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.login.ok"),
                    HttpStatus.OK
            );

            response.setData(loginResponse);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    commonUtil.getResponseMessage("resp.tcba.login.fail"),
                    HttpStatus.UNAUTHORIZED
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
