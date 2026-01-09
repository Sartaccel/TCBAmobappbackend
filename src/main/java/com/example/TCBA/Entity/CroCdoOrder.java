package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "containerOrder_mob")
@Data
public class CroCdoOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderType;
    private String depot;
    private String linerName;
    private String transporterName;
    private String svcType;
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ContainerDetail> containers = new ArrayList<>();

    private LocalDateTime createdAt;
}
