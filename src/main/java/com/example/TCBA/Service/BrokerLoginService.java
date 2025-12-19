package com.example.TCBA.Service;
import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrokerLoginService {

    private final BrokerLoginRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(String email, String rawPassword) {

        BrokerLogin broker = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(rawPassword, broker.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!Boolean.TRUE.equals(broker.getIsActive())) {
            throw new RuntimeException("Account inactive");
        }

        String token = jwtService.generateToken(broker.getEmail());

        return new LoginResponse(
                token,
                broker.getStackHolderId()
        );
    }
}

