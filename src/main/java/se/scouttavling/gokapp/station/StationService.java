package se.scouttavling.gokapp.station;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.security.User;

import java.util.List;
import java.util.Optional;

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
        return stationRepository.save(station);
    }

    public Optional<Station> getStationById(Integer id) {
        return stationRepository.findById(id);
    }

    public void delete(Integer id) {
        stationRepository.deleteById(id);
    }
}
