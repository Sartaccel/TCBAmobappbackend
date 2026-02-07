package com.example.TCBA.Service;

import com.example.TCBA.Config.CarryzenApiConfig;
import com.example.TCBA.Request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@RequiredArgsConstructor
@Service
public class CarryzenApiClient {

    private final CarryzenTokenService tokenService;
    private final RestTemplate restTemplate;
    private final CarryzenApiConfig apiConfig;

    private HttpHeaders createHeaders() {

        String token = tokenService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }


    public ResponseEntity<String> sendDoEntry(
            List<DoApiRequest> request) {

        return restTemplate.postForEntity(
                apiConfig.getIn_out_Url(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }

    public ResponseEntity<String> sendRoEntry(
            List<RoApiRequest> request) {

        return restTemplate.postForEntity(
                apiConfig.getRo_entry_url(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }

    public ResponseEntity<String> fetchGateContainers(GateContainerSearchRequest request) {

        return restTemplate.postForEntity(
                apiConfig.getView_gated_containers(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }

    public ResponseEntity<String> approveContainers(ContainerApproveRequest request) {

        return restTemplate.postForEntity(
                apiConfig.getApprove_reject_Pending_entries(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }

    public ResponseEntity<String> pendingContainers(GateContainerSearchRequest request) {

        return restTemplate.postForEntity(
                apiConfig.getPending_CHA_Approval(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }

    public ResponseEntity<String> fetchDoRoEntries(DoRoEntriesSearchRequest request) {

        return restTemplate.postForEntity(
                apiConfig.getView_DoRoEntries(),
                new HttpEntity<>(request, createHeaders()),
                String.class
        );
    }
    public ResponseEntity<String> fetchChaDashboard(ChaDashboardRequest request) {

        return restTemplate.postForEntity(
                apiConfig.getCha_dashboard(),
                new HttpEntity<>(request, createHeaders()),

                String.class
        );
    }

    public void updatePaymentAfterApproval(
            CarryzenPaymentUpdateRequest carryzenReq) {

        HttpEntity<CarryzenPaymentUpdateRequest> entity =
                new HttpEntity<>(carryzenReq, createHeaders());

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiConfig.getPaymentApproval(),
                        HttpMethod.POST,
                        entity,
                        String.class
                );


        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("THIRD_PARTY_UPDATE_FAILED");
        }
    }

}

