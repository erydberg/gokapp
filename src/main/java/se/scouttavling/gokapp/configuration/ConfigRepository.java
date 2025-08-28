package se.scouttavling.gokapp.configuration;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ConfigRepository extends JpaRepository<Config, Integer> {

    // Example: custom query to always fetch the latest config
    Config findTopByOrderByIdDesc();
}

