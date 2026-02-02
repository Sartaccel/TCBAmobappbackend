package com.example.TCBA.Controller;

import com.example.TCBA.Entity.ClientCredential;
import com.example.TCBA.Request.ClientTokenRequest;
import com.example.TCBA.Service.ClientAuthService;
import com.example.TCBA.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcba/oauth")
@RequiredArgsConstructor
public class OAuthTokenController {

    private final ClientAuthService clientAuthService;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> generateToken(
            @RequestBody ClientTokenRequest request
    ) {

        ClientCredential client =
                clientAuthService.validateClient(
                        request.getClientId(),
                        request.getClientSecret()
                );

        String token =
                jwtService.generateClientAccessToken(
                        client.getClientId()
                );

        return ResponseEntity.ok(
                Map.of(
                        "access_token", token,
                        "token_type", "Bearer",
                        "expires_in", "10 Minutes"
                )
        );

    }
}
