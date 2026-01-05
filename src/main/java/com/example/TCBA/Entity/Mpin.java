package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "broker_mpin")
@Data
public class Mpin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "broker_id", unique = true, nullable = false)
    private BrokerLogin broker;

    @Column(nullable = false)
    private String mpinHash;

    private int attempts;
    private boolean locked;
}


