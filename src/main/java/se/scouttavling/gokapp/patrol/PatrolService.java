package se.scouttavling.gokapp.patrol;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.station.Station;
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

    public List<Patrol> findAllSorted(String sort, String dir) {
        List<Patrol> patrols = patrolRepository.findAllWithScoresSorted();

        if (sort == null || sort.isBlank()) {
            sort = "patrolName";
        }
        if (dir == null || dir.isBlank()) {
            dir = "asc";
        }

        Comparator<Patrol> comparator = switch (sort) {
            case "patrolName" -> PatrolComparator.BY_NAME;
            case "track" -> PatrolComparator.BY_TRACK;
            case "troop" -> PatrolComparator.BY_TROOP;
            case "totalScore" -> PatrolComparator.BY_SCORE;
            case "latestScore" -> PatrolComparator.BY_LATEST_SCORE;
            default -> PatrolComparator.BY_NAME; // fallback
        };

        if ("desc".equalsIgnoreCase(dir)) {
            comparator = comparator.reversed();
        }

        patrols.sort(comparator);
        return patrols;
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

        return patrolRepository.findAllWithoutScoreOnStation(stationId);
    }


    public List<Patrol> getAllPatrolsSortByName() {

        return patrolRepository.findAllByOrderByPatrolNameAsc();
    }


    public List<Patrol> getAllPatrolsWithScores() {
        return patrolRepository.findAllWithScoresSorted();
    }


    public List<Patrol> getAllPatrolsWithScoresAndStations() {
        return patrolRepository.findAllWithScoresAndStationsSorted();
    }


    public Optional<Patrol> getPatrolByIdWithScores(Integer patrolId) {

        return patrolRepository.findPatrolByIdWithScores(patrolId);
    }


    public void updateStatus(Integer id, Status status) {
        Patrol patrol = getPatrolById(id).orElseThrow(() -> new IllegalArgumentException("Patrullen hittades inte"));
        patrol.setStatus(status);
        patrolRepository.save(patrol);
    }
}
