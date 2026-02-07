package com.example.TCBA.Repository;

import com.example.TCBA.Entity.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminSettingsRepository
        extends JpaRepository<AdminSettings, Long> {

    Optional<AdminSettings> findBySettingsName(String settingsName);
}

