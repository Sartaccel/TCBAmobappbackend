package com.example.TCBA.Controller;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.ChangePasswordRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Response.StackHolderProfileResponse;
import com.example.TCBA.Service.BrokerLoginService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/broker")
@RequiredArgsConstructor
public class BrokerProfileController {

    private final BrokerLoginService service;
    private final CommonUtil commonUtil;
    private final BrokerLoginRepository repository;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {

        try {
            String email = getAuthenticatedEmail();

            StackHolderProfileResponse profile =
                    service.getProfile(email);

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    commonUtil.getResponseMessage("resp.tcba.profile.fetch.ok"),
                    HttpStatus.OK,"OK"
            );

            response.setData(profile);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    commonUtil.getResponseMessage("resp.tcba.profile.fetch.fail"),
                    HttpStatus.UNAUTHORIZED,"FETCH_FAILED"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal String email,
            @RequestBody ChangePasswordRequest request) {

        try {
            // 🔹 username assumed as email
            BrokerLogin user = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println(user);
            // 🔹 call your service
            service.changePassword(user.getId(), request);

            ApiResponse response = new ApiResponse(
                    "SUCCESS",
                    "Password changed successfully",
                    HttpStatus.OK,""
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse response = new ApiResponse(
                    "FAILURE",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST,""
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
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

