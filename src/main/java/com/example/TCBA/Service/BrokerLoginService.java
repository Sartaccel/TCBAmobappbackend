package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import com.example.TCBA.Entity.PaymentDetails;
import com.example.TCBA.Entity.StackHolderContact;
import com.example.TCBA.Exception.AppException;
import com.example.TCBA.Exception.ErrorCode;
import com.example.TCBA.Repository.BrokerLoginRepository;
import com.example.TCBA.Repository.MpinRepository;
import com.example.TCBA.Repository.PaymentDetailsRepository;
import com.example.TCBA.Repository.StackHolderContactRepository;
import com.example.TCBA.Request.ChangePasswordRequest;
import com.example.TCBA.Response.LoginResponse;
import com.example.TCBA.Response.StackHolderProfileResponse;
import com.example.TCBA.Util.AesEncryptionUtil;
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
    private final AesEncryptionUtil util;

    public LoginResponse login(String email, String rawPassword) {

        BrokerLogin broker = repository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(rawPassword, broker.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!Boolean.TRUE.equals(broker.getIsActive())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        String accessToken = jwtService.generateAccessToken(broker.getEmail(),
                broker.getStackHolderId(),
                broker.getLegalName());
        String refreshToken = jwtService.generateRefreshToken(broker.getEmail());

        boolean hasMpin = mpinRepo.existsByBroker(broker);

        return new LoginResponse(
                accessToken,
                refreshToken,
                hasMpin, broker.getStackHolderId()
        );
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {

        BrokerLogin CHA = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getOldPassword(), CHA.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        CHA.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(CHA);
    }

    public StackHolderProfileResponse getProfile(String email) {

        BrokerLogin broker = repository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        PaymentDetails payment =
                paymentDetailsRepo.findTopByStackHolders(broker)
                        .orElse(null);

        StackHolderContact contact =
                contactRepo.findTopByStackHolders(broker)
                        .orElse(null);

        return StackHolderProfileResponse.builder()
                // BASIC
                .stakeHolderId(broker.getStackHolderId())
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .legalName(broker.getLegalName())
                .entityType(broker.getEntityType())
                .incorporationDate(broker.getIncorporationDate())
                .gst(util.decrypt(broker.getGst()))
                .pan(util.decrypt(broker.getPan()))
                .license(util.decrypt(broker.getLicense()))
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
}

