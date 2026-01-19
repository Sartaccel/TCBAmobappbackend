package com.example.TCBA.Service;

import com.example.TCBA.Config.CarryzenApiConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class CarryzenTokenService {

    private String token;
    private Instant expiry;

    private final RestTemplate restTemplate;
    private final CarryzenApiConfig apiConfig;

    public CarryzenTokenService(RestTemplate restTemplate, CarryzenApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    // Main entry point
    public synchronized String getToken() {
        if (token == null || expiry == null || Instant.now().isAfter(expiry)) {
            login();
        }
        return token;
    }

    // Force refresh (used when API returns 401)
    public synchronized void forceRefresh() {
        this.token = null;
        this.expiry = null;
        login();
    }

    private void login() {

        Map<String, String> body = Map.of(
                "clientKey", apiConfig.getClientKey(),
                "loginCode", apiConfig.getLoginCode(),
                "loginUserCompany", apiConfig.getCompany()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiConfig.getLoginUrl(), entity, Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Carryzen Login API failed");
        }

        Map<String, Object> responseBody = response.getBody();

        Object accessToken = responseBody.get("accessToken");
        Object expiresIn = responseBody.get("expiresIn");

        if (accessToken == null || expiresIn == null) {
            throw new RuntimeException("Invalid login response from Carryzen API");
        }

        this.token = accessToken.toString();

        long expirySeconds = parseExpiryToSeconds(expiresIn.toString());

        // add safety buffer of 30 seconds
        this.expiry = Instant.now().plusSeconds(expirySeconds - 30);
    }

    // Parses "60 minutes" → 3600 seconds
    private long parseExpiryToSeconds(String expiresIn) {

        String[] parts = expiresIn.split(" ");
        long value = Long.parseLong(parts[0]);
        String unit = parts[1].toLowerCase();

        if (unit.startsWith("minute")) {
            return value * 60;
        } else if (unit.startsWith("hour")) {
            return value * 3600;
        } else if (unit.startsWith("second")) {
            return value;
        } else {
            throw new IllegalArgumentException("Unknown expiresIn format: " + expiresIn);
        }
    }
}
