package com.example.TCBA.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settings_name", nullable = false, unique = true)
    private String settingsName;

    @Column(name = "settings_value", nullable = false)
    private String settingsValue;   // ✅ IMPORTANT

    @Column(name = "created_on", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private LocalDateTime updatedOn;
}
