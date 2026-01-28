package se.scouttavling.gokapp.station;

import org.springframework.data.jpa.repository.JpaRepository;
import se.scouttavling.gokapp.security.User;

import java.util.List;


public interface StationRepository extends JpaRepository<Station, Integer> {



    List<Station> findByStationUser(User user);

}

