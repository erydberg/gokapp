package se.scouttavling.gokapp.track;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public List<Track> findAllTracks() {
        return trackRepository.findAll();
    }

    public Optional<Track> findTrackById(Integer id) {
        return trackRepository.findById(id);
    }

    public Track saveTrack(Track track) {
        return trackRepository.save(track);
    }

    public void deleteTrack(Integer id) {
        trackRepository.deleteById(id);
    }

}

