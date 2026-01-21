package se.scouttavling.gokapp.print;

import se.scouttavling.gokapp.patrol.Patrol;

import java.util.List;

public record StartStation(int stationNumber, String name, List<Patrol> patrols) {
}
