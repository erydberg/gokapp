package se.scouttavling.gokapp.station;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StationRepository extends JpaRepository<Station, Integer> {
    // You can add custom queries here if needed
}

