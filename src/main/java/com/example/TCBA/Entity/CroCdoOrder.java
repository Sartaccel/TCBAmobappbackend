package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "container_order_mob")
@Data
public class CroCdoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginCode;

    private String entryType;

    private String entryNumber;

    private LocalDateTime entryDate;

    private String containerNo;

    private String containerSize;

    private String sealNo;

    private String movementType;

    private String yardCode;

    private String chaCode;

    private String linerCode;

    private String yardCompanyName;

    private String chaCompanyName;

    private String lineCompanyName;

    private String transportName;

    private String transportCode;

    private Integer totalContainers;

    private String referenceId;

    private String duplicateKey;

    private String approvalStatus;

    @Column(name = "count_20ft")
    private Integer count20ft;

    @Column(name = "count_40ft")
    private Integer count40ft;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ContainerDetail> containers = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String paymentStatus;

}
