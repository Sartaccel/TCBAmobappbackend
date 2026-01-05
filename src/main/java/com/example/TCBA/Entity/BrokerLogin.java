package com.example.TCBA.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

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

    @Column(name = "stack_holder_id", length = 100)
    private String stackHolderId;

    @Column(name = "wallet_id", unique = true)
    private String walletId;

//    @Column(name = "stack_holder_company_name", length = 150)
//    private String stackHolderCompanyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "stack_holder_type_id",
            foreignKey = @ForeignKey(name = "fk_stack_holder_type")
    )

    private StackHoldersType stackHoldersType;

    @Column(nullable = false)
    private String firstName;

//    @Column(name = "payment_setting_id")
//    private Long paymentSettingId;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // encrypted

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "profile_photo")
    private String profilePhoto; // URL / S3 path

    @Column(name = "license_number", nullable = false, unique = true)
    private String license;

    @Column(name = "gst_number", unique = true)
    private String gst;

//    @Column(name = "upi_id", unique = true)
//    private String upiId;

    @Column(name = "primary_upi_id", unique = true)
    private String primaryUpiId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
