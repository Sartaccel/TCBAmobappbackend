package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transport")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transport_name", nullable = false)
    private String transportName;

    @Column(name = "transport_code", unique = true)
    private String transportCode;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}