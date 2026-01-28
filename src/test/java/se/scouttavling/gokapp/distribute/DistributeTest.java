package se.scouttavling.gokapp.distribute;

import org.junit.jupiter.api.Test;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistributeTest {


    @Test
    public void shouldDistributePatrolsEven() {

        List<Station> stations = createStations(List.of("Kontroll 1", "Kontroll 2", "Kontroll 3"));
        Track trackSparare = new Track(1, "spårare");
        List<Patrol> spararePatrols = createPatrols(List.of("spårare 1", "spårare 2", "spårare 3", "spårare 4", "spårare 5"), trackSparare);
        Track trackUpptackare = new Track(2, "upptäckare");
        List<Patrol> upptackarePatrols = createPatrols(List.of("upptäckare 1", "upptäckare 2"), trackUpptackare);

        List<Patrol> allPatrols = new ArrayList<>();
        allPatrols.addAll(spararePatrols);
        allPatrols.addAll(upptackarePatrols);

        Distribute distribute = new Distribute(allPatrols, stations);
        String msg = distribute.distributePatrols();


        Map<String, Long> stationCounts = allPatrols.stream()
                .filter(patrol -> patrol.getStartStation() != null)
                .collect(Collectors.groupingBy(
                        patrol -> patrol.getStartStation().getStationName(),
                        Collectors.counting()
                ));

        assertEquals("7 patruller fördelade på 3 kontroller.", msg);
        assertEquals(3, stationCounts.get("Kontroll 1"));
        assertEquals(2, stationCounts.get("Kontroll 2"));
        assertEquals(2, stationCounts.get("Kontroll 3"));
    }


    @Test
    public void shouldReturnMessageWhenNoStations() {

        Track track = new Track(1, "Spårare");
        List<Patrol> patrols = createPatrols(List.of("Patrull 1"), track);
        Distribute distribute = new Distribute(patrols, null);
        String msg = distribute.distributePatrols();
        assertEquals("Det behöver finnas kontroller för att kunna använda den här funktionen.", msg);
    }


    @Test
    public void shouldReturnMessageWhenNoPatrols() {
        List<Station> stations = createStations(List.of("Kontroll 1"));
        Distribute distribute = new Distribute(null, stations);
        String msg = distribute.distributePatrols();
        assertEquals("Det finns inga patruller att fördela.", msg);
    }


    @Test
    public void shouldReturnMessageWhenNoPatrolsOnOneTrack() {

        Track track = new Track(1, "Spårare");
        List<Station> stations = createStations(List.of("Kontroll 1"));
        Distribute distribute = new Distribute(null, stations, track.getName());
        String msg = distribute.distributePatrols();
        assertEquals("Det finns inga patruller för Spårare", msg);
    }


    private List<Station> createStations(List<String> stationNames) {

        List<Station> stations = new ArrayList<>();
        int stationNumber = 0;
        for (String name : stationNames) {
            Station station = Station.builder().stationNumber(stationNumber).stationName(name).build();
            stations.add(station);
            stationNumber++;
        }

        return stations;
    }

    private List<Patrol> createPatrols(List<String> patrolNames, Track track) {

        return patrolNames.stream().map(patrolName -> Patrol.builder()
                        .patrolName(patrolName)
                        .track(track)
                        .build())
                .toList();
    }

}