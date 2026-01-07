package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cro_cdo_orders_mob")
@Data
public class CroCdoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderType; // CRO / CDO

    private String depot;
    private String linerName;

    // CRO fields
    private Integer noOfContainer;
    private Integer container20ft;
    private Integer container40ft;

    // CDO fields
    private String containerNumber;
    private String containerSize;
    private String customerName;
    private String sealNo;
    private String svcType;

    private String transporterName;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

