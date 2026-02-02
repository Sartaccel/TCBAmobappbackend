package com.example.TCBA.Service;

import com.example.TCBA.Entity.ClientCredential;
import com.example.TCBA.Repository.ClientCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAuthService {

    private final ClientCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ClientCredential validateClient(
            String clientId,
            String rawSecret
    ) {

        ClientCredential client =
                repository.findByClientIdAndActiveTrue(clientId)
                        .orElseThrow(() ->
                                new RuntimeException("INVALID_CLIENT_ID"));

        // ✅ bcrypt comparison
        if (!passwordEncoder.matches(
                rawSecret,
                client.getClientSecret()
        )) {
            throw new RuntimeException("INVALID_CLIENT_SECRET");
        }

        return client;
    }
}
