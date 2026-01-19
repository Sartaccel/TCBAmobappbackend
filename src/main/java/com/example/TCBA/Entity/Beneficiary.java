package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "beneficiary",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"upiId"}),
                @UniqueConstraint(columnNames = {"bankAccountLast4", "ifscCode"})
        }
)
@Data
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;

    private String upiId;

    private String bankAccountLast4;
    private String ifscCode;

    @Column(nullable = false)
    private String contactId;

    @Column(nullable = false)
    private String fundAccountId;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
