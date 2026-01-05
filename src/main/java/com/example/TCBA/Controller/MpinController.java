package com.example.TCBA.Controller;

import com.example.TCBA.Request.ChangeMpinRequest;
import com.example.TCBA.Request.SetMpinRequest;
import com.example.TCBA.Request.VerifyMpinRequest;
import com.example.TCBA.Response.ApiResponse;
import com.example.TCBA.Service.MpinService;
import com.example.TCBA.Util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mpin")
@RequiredArgsConstructor
public class MpinController {

    private final MpinService mpinService;
    private final CommonUtil commonUtil;

    // SET MPIN
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse> setMpin(
            @RequestBody SetMpinRequest request) {

        String email = getAuthenticatedEmail();

        mpinService.setMpin(email, request);

        ApiResponse response = new ApiResponse(
                "SUCCESS",
                commonUtil.getResponseMessage("resp.tcba.mpin.set.ok"),
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    // VERIFY MPIN
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyMpin(
            @RequestBody VerifyMpinRequest request) {

        String email = getAuthenticatedEmail();

        mpinService.verifyMpin(email, request.getMpin());

        ApiResponse response = new ApiResponse(
                "SUCCESS",
                commonUtil.getResponseMessage("resp.tcba.mpin.verify.ok"),
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    // CHANGE MPIN
    @PostMapping("/change")
    public ResponseEntity<ApiResponse> changeMpin(
            @RequestBody ChangeMpinRequest request) {

        String email = getAuthenticatedEmail();

        mpinService.changeMpin(email, request);

        ApiResponse response = new ApiResponse(
                "SUCCESS",
                commonUtil.getResponseMessage("resp.tcba.mpin.change.ok"),
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    // COMMON AUTH METHOD
    private String getAuthenticatedEmail() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {

            throw new RuntimeException(
                    commonUtil.getResponseMessage("resp.tcba.mpin.unauth")
            );
        }

        return auth.getName(); // email from JWT
    }
}
