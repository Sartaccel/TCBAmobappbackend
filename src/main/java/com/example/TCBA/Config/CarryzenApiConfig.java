package com.example.TCBA.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
public class CarryzenApiConfig {

    @Value("${carryzen.client-key}")
    private String clientKey;

    @Value("${carryzen.login-code}")
    private String loginCode;

    @Value("${carryzen.company}")
    private String company;

    private final String baseUrl = "https://api.carryzen.com";
    private final String loginUrl = "https://carryzen.co.in/carryzen/tcba/tcba_generateToken";
    private final String in_out_Url = "https://carryzen.co.in/carryzen/tcba/tcba_doroentry";
    private final String view_DoRoEntries = "https://carryzen.co.in/carryzen/tcba/tcba_getDoRoEntries";
    private final String view_gated_containers = "https://carryzen.co.in/carryzen/tcba/tcba_getGateContainersForCreditCHA";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

