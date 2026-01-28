package se.scouttavling.gokapp.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.track.Track;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExportCsvServiceTest {

    private ExportCsvService exportService;
    private Track track1;
    private Track track2;
    private Station station1;
    private Station station2;
    private Station waypointStation;

    @BeforeEach
    void setUp() {
        exportService = new ExportCsvService();

        track1 = new Track(1, "Spårare");
        track2 = new Track(2, "Upptäckare");

        station1 = Station.builder()
                .stationNumber(1)
                .stationName("Eldning")
                .waypoint(false)
                .build();

        station2 = Station.builder()
                .stationNumber(2)
                .stationName("Knopar")
                .waypoint(false)
                .build();

        waypointStation = Station.builder()
                .stationNumber(3)
                .stationName("Checkpoint")
                .waypoint(true)
                .build();
    }

    @Test
    void exportMultipleTracksToCsv_shouldExportBasicResults() {
        // Arrange
        Patrol patrol1 = createPatrol("Vargen", "Kår 1", 80, 15);
        Patrol patrol2 = createPatrol("Björnen", "Kår 2", 70, 10);

        Map<Track, List<Patrol>> trackResults = new LinkedHashMap<>();
        trackResults.put(track1, Arrays.asList(patrol1, patrol2));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsv(trackResults);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("Spårare")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Placering,Patrullnamn,Scoutkår,")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("1,Vargen,Kår 1,80,15,95")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("2,Björnen,Kår 2,70,10,80")));
    }

    @Test
    void exportMultipleTracksToCsv_shouldHandleMultipleTracks() {
        // Arrange
        Patrol patrol1 = createPatrol("Vargen", "Kår 1", 80, 15);
        Patrol patrol2 = createPatrol("Räven", "Kår 3", 60, 12);

        Map<Track, List<Patrol>> trackResults = new LinkedHashMap<>();
        trackResults.put(track1, Collections.singletonList(patrol1));
        trackResults.put(track2, Collections.singletonList(patrol2));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsv(trackResults);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("Spårare")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Upptäckare")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Vargen")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Räven")));
    }

    @Test
    void exportMultipleTracksToCsv_shouldEscapeCommasInNames() {
        // Arrange
        Patrol patrol = createPatrol("Vargen, den vilda", "Kår 1, Stockholm", 80, 15);

        Map<Track, List<Patrol>> trackResults = new LinkedHashMap<>();
        trackResults.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsv(trackResults);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("\"Vargen, den vilda\"")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("\"Kår 1, Stockholm\"")));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldIncludeStationScores() {
        // Arrange
        Patrol patrol = createPatrol("Vargen", "Kår 1", 80, 15);
        Score score1 = createScore(station1, 40, 8);
        Score score2 = createScore(station2, 40, 7);
        patrol.setScores(Set.of(score1, score2));

        List<Station> stations = Arrays.asList(station1, station2);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("K1-poäng,K1-stil")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("K2-poäng,K2-stil")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("1,Vargen,Kår 1,40,8,40,7,80,15,95")));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldHandleWaypoints() {
        // Arrange
        Patrol patrol = createPatrol("Vargen", "Kår 1", 80, 15);
        Score waypointScore = createWaypointScore(waypointStation, true);
        patrol.setScores(Set.of(waypointScore));

        List<Station> stations = Collections.singletonList(waypointStation);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("Milstolpe 3")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("passerat")));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldHandleUnvisitedWaypoint() {
        // Arrange
        Patrol patrol = createPatrol("Vargen", "Kår 1", 80, 15);
        Score waypointScore = createWaypointScore(waypointStation, false);
        patrol.setScores(Set.of(waypointScore));

        List<Station> stations = Collections.singletonList(waypointStation);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        String dataLine = lines.stream()
                .filter(line -> line.startsWith("1,Vargen"))
                .findFirst()
                .orElseThrow();

        // Should have empty waypoint field (just comma)
        System.out.println(dataLine);
        assertTrue(dataLine.matches(".*,80,15"));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldHandleMissingScores() {
        // Arrange
        Patrol patrol = createPatrol("Vargen", "Kår 1", 40, 8);
        Score score1 = createScore(station1, 40, 8);
        patrol.setScores(Set.of(score1));

        List<Station> stations = Arrays.asList(station1, station2);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        String dataLine = lines.stream()
                .filter(line -> line.startsWith("1,Vargen"))
                .findFirst()
                .orElseThrow();

        // Should have score for station1 but empty for station2
        System.out.println(dataLine);
        assertTrue(dataLine.contains("40,8,,,40,8,48"));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldIncludeStationInformation() {
        // Arrange
        Patrol patrol = createPatrol("Vargen", "Kår 1", 80, 15);
        List<Station> stations = Arrays.asList(station1, station2);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Collections.singletonList(patrol));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("Kontrollinformation")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("1 Eldning")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("2 Knopar")));
    }

    @Test
    void exportMultipleTracksToCsvComplete_shouldMaintainRankingOrder() {
        // Arrange
        Patrol patrol1 = createPatrol("Vargen", "Kår 1", 80, 15);
        Patrol patrol2 = createPatrol("Björnen", "Kår 2", 70, 10);
        Patrol patrol3 = createPatrol("Räven", "Kår 3", 60, 12);

        List<Station> stations = Collections.singletonList(station1);

        Map<Track, List<Patrol>> results = new LinkedHashMap<>();
        results.put(track1, Arrays.asList(patrol1, patrol2, patrol3));

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsvComplete(results, stations);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("1,Vargen")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("2,Björnen")));
        assertTrue(lines.stream().anyMatch(line -> line.startsWith("3,Räven")));
    }

    @Test
    void exportMultipleTracksToCsv_shouldHandleEmptyPatrolList() {
        // Arrange
        Map<Track, List<Patrol>> trackResults = new LinkedHashMap<>();
        trackResults.put(track1, Collections.emptyList());

        // Act
        ByteArrayInputStream result = exportService.exportMultipleTracksToCsv(trackResults);
        List<String> lines = readLines(result);

        // Assert
        assertTrue(lines.stream().anyMatch(line -> line.contains("Spårare")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("Placering,Patrullnamn,Scoutkår,")));
    }

    // Helper methods

    private Patrol createPatrol(String name, String troop, int scorePoints, int stylePoints) {
        Patrol patrol = new Patrol();
        patrol.setPatrolName(name);
        patrol.setTroop(troop);
        patrol.setScores(new HashSet<>());

        // Create scores that will sum to the desired totals
        // Assuming you have at least station1 and station2 available
        Score score1 = createScore(station1, scorePoints / 2, stylePoints / 2);
        Score score2 = createScore(station2, scorePoints - (scorePoints / 2), stylePoints - (stylePoints / 2));
        patrol.setScores(Set.of(score1, score2));

        return patrol;
    }

    private Score createScore(Station station, int scorePoint, int stylePoint) {
        Score score = new Score();
        score.setStation(station);
        score.setScorePoint(scorePoint);
        score.setStylePoint(stylePoint);
        return score;
    }

    private Score createWaypointScore(Station station, boolean visited) {
        Score score = new Score();
        score.setStation(station);
        score.setVisitedWaypoint(visited);
        return score;
    }

    private List<String> readLines(ByteArrayInputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV", e);
        }
    }
}