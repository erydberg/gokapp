package se.scouttavling.gokapp.export;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.print.QRCodeService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/export")
@RequiredArgsConstructor
public class ExportController {
    private final PatrolService patrolService;
    private final ConfigService configService;
    private final StationService stationService;
    private final TrackService trackService;
    private final ExportCsvService exportService;
    private final QRCodeService qrCodeService;

    @ModelAttribute("config")
    public Config loadConfig() {
        return configService.getCurrentConfig();
    }

    @GetMapping
    public String start() {

        return "print_start";
    }


    @GetMapping("/results-short")
    public ResponseEntity<Resource> exportShort() {
        List<Track> tracks = trackService.findAllTracks();
        Map<Track, List<Patrol>> results = new LinkedHashMap<>();


        for (Track track:tracks) {
            List<Patrol> patrolsOnTrack = patrolService.getAllPatrolsByTrackSortedByScore(track);
            results.put(track, patrolsOnTrack);
        }

        ByteArrayInputStream stream = exportService.exportMultipleTracksToCsv(results);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=competition_results.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }


    @GetMapping("/results-complete")
    public ResponseEntity<Resource> exportCsvComplete() {
        List<Track> tracks = trackService.findAllTracks();
        Map<Track, List<Patrol>> results = new LinkedHashMap<>();


        for (Track track:tracks) {
            List<Patrol> patrolsOnTrack = patrolService.getAllPatrolsByTrackSortedByScore(track);
            results.put(track, patrolsOnTrack);
        }

        List<Station> stations = stationService.getAll();


        ByteArrayInputStream stream = exportService.exportMultipleTracksToCsvComplete(results, stations);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=competition_results.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }
}
