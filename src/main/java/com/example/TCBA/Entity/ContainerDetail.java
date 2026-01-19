package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "container_details_mob")
@Data
public class ContainerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String containerNumber;
    private String containerSize;
    private String customerName;
    private String sealNo;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private CroCdoOrder order;
}

