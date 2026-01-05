package com.example.TCBA.Controller;

import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.StackHolderProfileResponse;
import com.example.TCBA.Service.BrokerLoginService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/broker")
@RequiredArgsConstructor
public class BrokerProfileController {

    private final BrokerLoginService service;
    private final CommonUtil commonUtil;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {

        try {
            String email = getAuthenticatedEmail();

            StackHolderProfileResponse profile =
                    service.getProfile(email);

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.profile.fetch.ok"),
                    HttpStatus.OK
            );

            response.setData(profile);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    commonUtil.getResponseMessage("resp.tcba.profile.fetch.fail"),
                    HttpStatus.UNAUTHORIZED
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    // 🔐 COMMON METHOD
    private String getAuthenticatedEmail() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null
                || auth instanceof AnonymousAuthenticationToken
                || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        return auth.getName(); // email from JWT
    }
}

