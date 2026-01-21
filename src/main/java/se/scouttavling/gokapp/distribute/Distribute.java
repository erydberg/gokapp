package se.scouttavling.gokapp.distribute;

import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.station.Station;

import java.util.List;

public class Distribute {

    int lastUsedStation;
    List<Patrol> patrols;
    List<Station> stations;
    String trackName = "";

    public Distribute(List<Patrol> patrols, List<Station> stations) {
        this.patrols = patrols;
        this.stations = stations;
    }

    public Distribute(List<Patrol> patrols, List<Station> stations, String trackName) {
        this.patrols = patrols;
        this.stations = stations;
        this.trackName = trackName;
    }


    public String distributePatrols() {
        if (stations.isEmpty()) {
            return "Det behöver finnas kontroller för att kunna använda den här funktionen.";
        }

        if (patrols.isEmpty() && !trackName.isEmpty()) {
            return String.format("Det finns inga patruller för %s", trackName);
        }

        if (patrols.isEmpty()) {
            return "Det finns inga patruller att fördela.";
        }

        int noOfStations = stations.size();
        int noOfPatrols = patrols.size();
        lastUsedStation = 0;

        for (Patrol patrol : patrols) {
            System.out.println("lastUsedStation " + lastUsedStation);
            patrol.setStartStation(stations.get(lastUsedStation));
            calculateLastUsedStation(stations);
        }

        if (trackName.isEmpty()) {
            return String.format("%d patruller fördelade på %d kontroller.", noOfPatrols, noOfStations);
        } else {
            return String.format("%d patruller (%s) fördelade på %d kontroller.", noOfPatrols, trackName, noOfStations);
        }
    }

    private void calculateLastUsedStation(List<Station> stations) {
        if (lastUsedStation < stations.size() - 1) {
            lastUsedStation++;
        } else {
            lastUsedStation = 0;
        }
    }
}
