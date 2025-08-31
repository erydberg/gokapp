package se.scouttavling.gokapp.patrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.track.Track;

import java.util.List;


public interface PatrolRepository extends JpaRepository<Patrol, Integer> {

    // Get all patrols ordered by patrolName ascending
    List<Patrol> findAllByOrderByPatrolNameAsc();

    // Get all patrols ordered by status
    List<Patrol> findAllByOrderByStatusAsc();

    // Get all patrols ordered by troop
    List<Patrol> findAllByOrderByTroopAsc();

    // Get all patrols ordered by track
    List<Patrol> findAllByOrderByTrackAsc();

    // Find patrols by Track entity
    List<Patrol> findByTrackOrderByPatrolNameAsc(Track track);

    // Find patrols by Track id
    List<Patrol> findByTrackIdOrderByPatrolNameAsc(Integer trackId);

    // Optional: if you want a single patrol by ID and throw if not found
    default Patrol getByIdOrThrow(Integer id) {
        return findById(id).orElseThrow(() -> new RuntimeException("Patrol not found with id " + id));
    }

    //OLD
//    @Query("SELECT p FROM Patrol p LEFT JOIN FETCH p.scores s WHERE s.station.stationId <> :stationId OR s IS NULL")
//    List<Patrol> findPatrolsLeftOnStation(@Param("stationId") Integer stationId);

    //my old query don't work anymore so this gives every patrol that has a score and then it will be filtered in PatrolService
    @Query("SELECT DISTINCT p FROM Patrol p LEFT JOIN FETCH p.scores s")
    List<Patrol> findAllWithScores();
}