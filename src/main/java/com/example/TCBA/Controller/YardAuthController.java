package com.example.TCBA.Controller;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Request.YardLoginRequest;
import com.example.TCBA.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/tcba/yard")
@RequiredArgsConstructor
public class YardAuthController {

    private final BrokerLoginRepository brokerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> yardLogin(
            @RequestBody YardLoginRequest request
    ) {

        // 1️⃣ Find yard by email
        BrokerLogin yard = brokerRepo
                .findByEmail(request.getEmail())
                .orElse(null);

        if (yard == null) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid email or password");
        }

        // 2️⃣ Check password
        if (!passwordEncoder.matches(
                request.getPassword(),
                yard.getPassword())) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid email or password");
        }

        // 3️⃣ Generate JWT with yardId
        String token = jwtService.generateYardToken(yard);

        // 4️⃣ Return token
        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "accessToken", token
                )
        );
    }
}

