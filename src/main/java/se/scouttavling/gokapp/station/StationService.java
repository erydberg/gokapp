package se.scouttavling.gokapp.station;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.security.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public List<Station> getAll() {
        return stationRepository.findAll(Sort.by(Sort.Direction.ASC, "stationNumber"));
    }

    public List<Station> getForUser(User user) {

        return stationRepository.findByStationUser(user);
    }

    public Station save(Station station) {
        if (station.isAllTracks() && station.getTracks() != null) {
            station.getTracks().clear();
        }
        if (Boolean.TRUE.equals(station.getUseDistinctScores())) {
            List<Integer> values = station.getDistinctScoreList();
            if (!values.isEmpty()) {
                station.setDistinctScores(values.stream().map(String::valueOf).collect(Collectors.joining(",")));
                station.setMinScore(values.getFirst());
                station.setMaxScore(values.getLast());
            }
        }
        return stationRepository.save(station);
    }

    public Optional<Station> getStationById(Integer id) {
        return stationRepository.findById(id);
    }

    public Optional<Station> getStationByIdWithTracks(Integer id) {
        return stationRepository.findByIdWithTracks(id);
    }

    public void delete(Integer id) {
        stationRepository.deleteById(id);
    }
}
