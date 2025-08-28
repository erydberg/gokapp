package se.scouttavling.gokapp.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {

    // Example custom query method
    Track findByName(String name);
}

