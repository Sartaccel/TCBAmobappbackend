package com.example.TCBA.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stack_holders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerLogin {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @Column(name = "stake_holder_id", length = 100, unique = true)
    private String stakeHolderId;

    private String legalName;
    private String entityType;

    private LocalDate incorporationDate;

    @Column(unique = true)
    private String pan;

    @Column(name = "aadhar", unique = true)
    private String aadhaar;

    // Registered Address
    private String regLine1;
    private String regLine2;
    private String regCity;
    private String regState;
    private String regCountry;
    private String regPincode;

    // Operating Address
    private String opLine1;
    private String opLine2;
    private String opCity;
    private String opState;
    private String opCountry;
    private String opPincode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stack_holder_type_id",
            foreignKey = @ForeignKey(name = "fk_stack_holder_type")
    )
    private StackHoldersType stackHoldersType;

    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password; // encrypted

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private  String alternatePhoneNumber;

    private String primaryAccountHolderName;
    private String primaryBankName;
    private String primaryAccountNumber;
    private String primaryIfsc;
    private String primaryAccountType;


    @Column(name = "profile_photo")
    private String profilePhoto; // URL / S3 path

    @Column(name = "license_number", unique = true)
    private String license;

    @Column(name = "gst_number", unique = true)
    private String gst;

    @Column(name = "primary_upi_id", unique = true)
    private String primaryUpiId;

    @Column(name = "wallet_id", unique = true)
    private String walletId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "e_invoice")
    private Boolean eInvoice = false;

    @Column(name = "dob")
    private LocalDate dob;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Builder.Default
    @OneToMany(
            mappedBy = "stackHolders",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PaymentDetails> paymentCycle = new HashSet<>();
    @Builder.Default
    @OneToMany(
            mappedBy = "stackHolders",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<StackHolderContact> stackHolderContact = new HashSet<>();
}