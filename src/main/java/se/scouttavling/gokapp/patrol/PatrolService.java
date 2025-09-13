package se.scouttavling.gokapp.patrol;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.track.Track;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatrolService {

    private final PatrolRepository patrolRepository;

    public List<Patrol> getAllPatrols() {
        return patrolRepository.findAll();
    }

    public Optional<Patrol> getPatrolById(Integer id) {
        return patrolRepository.findById(id);
    }

    public void deletePatrolById(Integer id) {
        patrolRepository.deleteById(id);
    }

    @Transactional
    public Patrol save(Patrol patrol) {
        if (patrol.getPatrolId() == null) {
            patrol.setDateRegistered(LocalDateTime.now());
        }
        return patrolRepository.save(patrol);
    }


    public List<Patrol> getAllPatrolsByTrack(Track track) {
        return patrolRepository.findByTrackOrderByPatrolNameAsc(track)
                .stream()
                .sorted(Comparator.comparing(Patrol::getTotalScore).reversed()) // sort by score descending
                .toList();
    }

    public List<Patrol> getAllPatrolsByTrackSortedByScore(Track track) {

        List<Patrol> patrols = patrolRepository.findByTrackIdWithScores(track.getId());
        Collections.sort(patrols);

        return patrols;
    }


    public List<Patrol> allPatrolsLeftOnStation(Integer stationId) {

        return patrolRepository.findAllWithoutScoreForStation(stationId);

        /* In memory version
        return patrolRepository.findAllWithScores().stream()
                .filter(p -> p.getScores().stream()
                        .noneMatch(s -> s.getStation().getId().equals(stationId)))
                .toList();

         */
    }

    public List<Patrol> getAllPatrolsSortByName() {

        return patrolRepository.findAllByOrderByPatrolNameAsc();
    }
}
