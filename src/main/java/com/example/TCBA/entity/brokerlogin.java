package com.example.TCBA.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class brokerlogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

}
