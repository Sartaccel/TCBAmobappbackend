package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "entry_sequence")
@Data
public class EntrySequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prefix;

    private Long nextVal;
}

