package com.example.TCBA.Service;
import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.PaymentDetails;
import com.example.TCBA.Entity.StackHolderContact;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.MpinRepository;
import com.example.TCBA.Repository.PaymentDetailsRepository;
import com.example.TCBA.Repository.StackHolderContactRepository;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Response.StackHolderProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrokerLoginService {

    private final BrokerLoginRepository repository;
    private final PaymentDetailsRepository paymentDetailsRepo;
    private final StackHolderContactRepository contactRepo;
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

        PaymentDetails payment =
                paymentDetailsRepo.findTopByStackHolders(broker)
                        .orElse(null);

        StackHolderContact contact =
                contactRepo.findTopByStackHolders(broker)
                        .orElse(null);

        return StackHolderProfileResponse.builder()
                // BASIC
                .stackHolderId(broker.getStackHolderId())
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .legalName(broker.getLegalName())
                .entityType(broker.getEntityType())
                .incorporationDate(broker.getIncorporationDate())
                .gst(broker.getGst())
                .pan(broker.getPan())
                .license(broker.getLicense())
                .email(broker.getEmail())
                .phoneNumber(broker.getPhoneNumber())
                .alternatePhoneNumber(broker.getAlternatePhoneNumber())

                // CONTACT
                .contactEmail(contact != null ? contact.getContactEmail() : null)
                .contactPhone(contact != null ? contact.getContactPhone() : null)
                .designation(contact != null ? contact.getDesignation() : null)

                // PAYMENT
                .paymentTerms(payment != null ? payment.getPaymentTerms() : null)
                .invoiceApprovalRequired(payment != null ? payment.getInvoiceApprovalRequired() : null)
                .minimumBalance(payment != null ? payment.getMinimumBalance() : null)
                .tdsPercent(payment != null ? payment.getTdsPercent() : null)
                .transactionLimit(payment != null ? payment.getTransactionLimit() : null)
                .proformaInvoice(payment != null ? payment.getProformaInvoice() : null)

                .build();
    }


//    public StackHolderProfileResponse getProfile(String email) {
//
//        BrokerLogin broker = repository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        PaymentDetails payment =
//                paymentDetailsRepo.findTopByStackHolders(broker)
//                        .orElse(null);
//
//        StackHolderContact contact =
//                contactRepo.findTopByStackHolders(broker)
//                        .orElse(null);
//
//        return new StackHolderProfileResponse(
//                broker.getStackHolderId(),
//                broker.getFirstName(),
//                broker.getLastName(),
//                broker.getGst(),
//                broker.getLicense(),
//                broker.getEmail(),
//                broker.getPhoneNumber(),
//        );
//    }

}

