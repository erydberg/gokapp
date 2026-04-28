package se.scouttavling.gokapp.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.scouttavling.gokapp.security.User;

import java.util.List;
import java.util.Optional;


public interface StationRepository extends JpaRepository<Station, Integer> {

    List<Station> findByStationUser(User user);

    @Query("SELECT s FROM Station s LEFT JOIN FETCH s.tracks WHERE s.id = :id")
    Optional<Station> findByIdWithTracks(@Param("id") Integer id);

}

