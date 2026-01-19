package com.example.TCBA.Service;

import com.example.TCBA.Config.CarryzenApiConfig;
import com.example.TCBA.Request.CroCdoOrderRequest;
import com.example.TCBA.Request.DoRoEntriesSearchRequest;
import com.example.TCBA.Request.GateContainerSearchRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CarryzenApiClient {

    private final CarryzenTokenService tokenService;
    private final RestTemplate restTemplate;
    private final CarryzenApiConfig apiConfig;

    public CarryzenApiClient(CarryzenTokenService tokenService,
                             RestTemplate restTemplate,
                             CarryzenApiConfig apiConfig) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
    }

    public ResponseEntity<String> sendDoRoEntry(CroCdoOrderRequest request) {

        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CroCdoOrderRequest> entity =
                new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(
                apiConfig.getIn_out_Url(),
                entity,
                String.class
        );
    }

    public ResponseEntity<String> fetchGateContainers(GateContainerSearchRequest request) {

        String token = tokenService.getToken();

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

        String token = tokenService.getToken();

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
}
