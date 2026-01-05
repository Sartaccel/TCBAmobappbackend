package com.example.TCBA.Service;
import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.MpinRepository;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Response.StackHolderProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrokerLoginService {

    private final BrokerLoginRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MpinRepository mpinRepo;

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
        boolean hasMpin = mpinRepo.existsByBroker(broker);

        return new LoginResponse(
                token,
                broker.getStackHolderId(),
                broker.getWalletId(),
                hasMpin

        );
    }

    public StackHolderProfileResponse getProfile(String email) {

        BrokerLogin broker = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new StackHolderProfileResponse(
                broker.getStackHolderId(),
                broker.getFirstName(),
                broker.getLastName(),
                broker.getGst(),
                broker.getLicense(),
                broker.getEmail(),
                broker.getPhoneNumber()
        );
    }

}

