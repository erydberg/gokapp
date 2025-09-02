package se.scouttavling.gokapp.configuration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationConfigRepository extends JpaRepository<RegistrationConfig, Integer> {

    // Example: custom query to always fetch the latest config
    RegistrationConfig findTopByOrderByIdDesc();
}
