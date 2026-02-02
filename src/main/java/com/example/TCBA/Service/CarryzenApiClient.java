package com.example.TCBA.Service;

import com.example.TCBA.Config.CarryzenApiConfig;
import com.example.TCBA.Request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@RequiredArgsConstructor
@Service
public class CarryzenApiClient {

    private final CarryzenTokenService tokenService;
    private final RestTemplate restTemplate;
    private final CarryzenApiConfig apiConfig;

    public ResponseEntity<String> sendDoEntry(
            List<DoApiRequest> request) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.postForEntity(
                apiConfig.getIn_out_Url(),
                new HttpEntity<>(request, headers),
                String.class
        );
    }

    public ResponseEntity<String> sendRoEntry(
            List<RoApiRequest> request) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.postForEntity(
                apiConfig.getRo_entry_url(),
                new HttpEntity<>(request, headers),
                String.class
        );
    }

    public ResponseEntity<String> fetchGateContainers(GateContainerSearchRequest request) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // If filters is null, it will not be sent in JSON
        HttpEntity<GateContainerSearchRequest> entity =
                new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(
                apiConfig.getView_gated_containers(),
                entity,
                String.class
        );
    }

    public ResponseEntity<String> fetchDoRoEntries(DoRoEntriesSearchRequest request) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // If filters is null, it will not be sent in JSON
        HttpEntity<DoRoEntriesSearchRequest> entity =
                new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(
                apiConfig.getView_DoRoEntries(),
                entity,
                String.class
        );
    }
    public ResponseEntity<String> fetchChaDashboard(ChaDashboardRequest request) {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChaDashboardRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(
                apiConfig.getCha_dashboard(),
                entity,
                String.class
        );
    }
}
