package se.scouttavling.gokapp.track;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrackConverter implements Converter<String, Track> {

    private final TrackRepository trackRepository;

    public TrackConverter(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public Track convert(String id) {
        return trackRepository.findById(Integer.parseInt(id)).orElse(null);
    }
}
