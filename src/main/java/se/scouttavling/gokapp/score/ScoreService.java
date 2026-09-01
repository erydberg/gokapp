package se.scouttavling.gokapp.score;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final StationRepository stationRepository;

    public List<Score> findAll() {
        return scoreRepository.findAll();
    }

    public Optional<Score> findById(Integer id) {
        return scoreRepository.findById(id);
    }

    public Score save(Score score) {
        // Re-fetch the station fresh rather than trusting whatever the request bound onto
        // score.getStation() - only its id is ever actually submitted by the forms, but this
        // guards against a tampered request that tries to smuggle other values in too.
        Station station = stationRepository.findById(score.getStation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        if (!station.isValidScorePoint(score.getScorePoint())) {
            throw new InvalidScoreException("Ogiltigt poäng (" + score.getScorePoint()
                    + ") för kontrollen " + station.getStationName() + ".");
        }
        if (!station.isValidStylePoint(score.getStylePoint())) {
            throw new InvalidScoreException("Ogiltigt stilpoäng (" + score.getStylePoint()
                    + ") för kontrollen " + station.getStationName() + ".");
        }

        score.setLastSaved(LocalDateTime.now());
        return scoreRepository.save(score);
    }

    public void deleteById(Integer id) {
        scoreRepository.deleteById(id);
    }

    // --- With fetch joins (ensures patrol/station are loaded) ---
    @Transactional
    public Optional<Score> findByIdWithPatrol(Integer id) {
        return scoreRepository.findByIdWithPatrol(id);
    }

    @Transactional
    public Optional<Score> findByIdWithStation(Integer id) {
        return scoreRepository.findByIdWithStation(id);
    }

    @Transactional
    public Optional<Score> findByIdWithPatrolAndStation(Integer id) {
        return scoreRepository.findByIdWithPatrolAndStation(id);
    }

    @Transactional
    public List<Score> findByPatrol(Integer patrolId) {
        return scoreRepository.findByPatrol_PatrolId(patrolId);
    }


    public List<Score> getScoresForStation(Integer stationId) {
        return scoreRepository.findByStation_IdOrderByLastSavedDesc(stationId);
    }
}