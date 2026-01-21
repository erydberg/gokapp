package se.scouttavling.gokapp.patrol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.scouttavling.gokapp.track.Track;

import java.util.List;
import java.util.Optional;


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

    //my old query don't work anymore so this gives every patrol that has a score and then it will be filtered in PatrolService
    @Query("SELECT DISTINCT p FROM Patrol p LEFT JOIN FETCH p.scores s")
    List<Patrol> findAllWithScores();

    @Query("SELECT DISTINCT p FROM Patrol p LEFT JOIN FETCH p.scores s ORDER BY p.patrolName ASC")
    List<Patrol> findAllWithScoresSorted();

    @Query("SELECT DISTINCT p FROM Patrol p LEFT JOIN FETCH p.scores s LEFT JOIN FETCH s.station ORDER BY p.patrolName ASC")
    List<Patrol> findAllWithScoresAndStationsSorted();

    @Query("SELECT p FROM Patrol p LEFT JOIN FETCH p.scores s WHERE p.patrolId = :patrolId")
    Optional<Patrol> findPatrolByIdWithScores(@Param("patrolId") Integer patrolId);


    @Query("SELECT p FROM Patrol p LEFT JOIN FETCH p.scores s LEFT JOIN FETCH s.station WHERE p.patrolId = :patrolId")
    Optional<Patrol> findPatrolByIdWithScoresAndStations(Integer patrolId);

    @Query("SELECT p FROM Patrol p WHERE NOT EXISTS (SELECT s FROM Score s WHERE s.patrol = p AND s.station.id = :stationId)")
    List<Patrol> findAllWithoutScoreOnStation(@Param("stationId") Integer stationId);

    @Query("SELECT DISTINCT p FROM Patrol p LEFT JOIN FETCH p.scores s LEFT JOIN FETCH s.station WHERE p.track.id = :trackId")
    List<Patrol> findByTrackIdWithScores(@Param("trackId") Integer trackId);

    @Query("SELECT p from Patrol p WHERE p.startStation.id = :id")
    List<Patrol> findByStartStation(@Param("id") Integer id);
}