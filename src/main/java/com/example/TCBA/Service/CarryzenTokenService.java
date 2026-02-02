package com.example.TCBA.Service;

import com.example.TCBA.Config.CarryzenApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class CarryzenTokenService {

    private String accessToken;
    private String refreshToken;
    private Instant accessExpiry;
    private Instant refreshExpiry;

    private final RestTemplate restTemplate;
    private final CarryzenApiConfig apiConfig;

    public CarryzenTokenService(RestTemplate restTemplate, CarryzenApiConfig apiConfig) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    // Main entry point
    public synchronized String getAccessToken() {

        if (accessToken == null || accessExpiry == null || Instant.now().isAfter(accessExpiry)) {
            refreshOrLogin();
        }
        return accessToken;
    }

    // Called when API returns 401
    public synchronized void handleUnauthorized() {
        refreshOrLogin();
    }

    // Decides whether to refresh or login
    private synchronized void refreshOrLogin() {

        if (refreshToken == null || refreshExpiry == null || Instant.now().isAfter(refreshExpiry)) {
            login();   // refresh expired → full login
        } else {
            refreshAccessToken();  // refresh still valid → regenerate tokens
        }
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
        parseAndStoreToken(response.getBody());

    }

    // 2️⃣ Refresh API (Bearer refresh token, empty body)
    private synchronized void refreshAccessToken() {

        log.info("Refreshing Carryzen token using refreshToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "refreshToken", refreshToken
        );

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiConfig.getRefreshUrl(),
                entity,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.warn("Refresh token failed. Performing fresh login.");
            login();
            return;
        }

        parseAndStoreToken(response.getBody());
    }

    // Parses login & refresh response
    private void parseAndStoreToken(Map<String, Object> body) {

        this.accessToken = body.get("accessToken").toString();
        this.refreshToken = body.get("refreshToken").toString();

        Instant accessExpiresAt = Instant.parse(body.get("accessExpiresAt").toString() + "Z");
        Instant refreshExpiresAt = Instant.parse(body.get("refreshExpiresAt").toString() + "Z");

        // Safety buffer
        this.accessExpiry = accessExpiresAt.minusSeconds(120);
        this.refreshExpiry = refreshExpiresAt.minusSeconds(120);

        log.info("Carryzen tokens updated. Access expires at {}", accessExpiry);
    }
}