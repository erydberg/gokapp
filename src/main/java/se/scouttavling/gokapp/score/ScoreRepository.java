package se.scouttavling.gokapp.score;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Integer> {

    // --- Default: Patrol and Station will be lazy unless accessed inside a @Transactional ---
    Optional<Score> findById(Integer id);

    // --- Fetch join for a single Score and its Patrol ---
    @Query("SELECT s FROM Score s JOIN FETCH s.patrol WHERE s.id = :id")
    Optional<Score> findByIdWithPatrol(@Param("id") Integer id);

    // --- Fetch join for a single Score and its Station ---
    @Query("SELECT s FROM Score s JOIN FETCH s.station WHERE s.id = :id")
    Optional<Score> findByIdWithStation(@Param("id") Integer id);

    // --- Fetch join for both Patrol and Station ---
    @Query("SELECT s FROM Score s JOIN FETCH s.patrol JOIN FETCH s.station WHERE s.id = :id")
    Optional<Score> findByIdWithPatrolAndStation(@Param("id") Integer id);


    // --- Using EntityGraph for all scores with Patrol and Station loaded ---
    @EntityGraph(attributePaths = {"patrol", "station"})
    List<Score> findAll();

    // --- Example: find all scores for a patrol, fetching stations eagerly ---
    @EntityGraph(attributePaths = {"station"})
    List<Score> findByPatrol_PatrolId(Integer patrolId);

    @EntityGraph(attributePaths = {"patrol"})
    List<Score> findByStation_IdOrderByLastSavedDesc(Integer stationId);
}

