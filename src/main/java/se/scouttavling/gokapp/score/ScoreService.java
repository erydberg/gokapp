package se.scouttavling.gokapp.score;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public List<Score> findAll() {
        return scoreRepository.findAll();
    }

    public Optional<Score> findById(Integer id) {
        return scoreRepository.findById(id);
    }

    public Score save(Score score) {
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