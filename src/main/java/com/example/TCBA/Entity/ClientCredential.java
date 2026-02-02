package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "client_credentials")
@Data
public class ClientCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String clientId;

    private String clientSecret;

    private String clientName;

    private Boolean active = true;
}

