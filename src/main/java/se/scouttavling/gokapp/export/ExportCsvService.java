package se.scouttavling.gokapp.export;

import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.track.Track;

import java.io.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class ExportCsvService {
    public static final String DELIMITER = ",";

    public ByteArrayInputStream exportMultipleTracksToCsv(Map<Track, List<Patrol>> trackResults) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            for (Map.Entry<Track, List<Patrol>> entry : trackResults.entrySet()) {
                Track track = entry.getKey();
                List<Patrol> patrols = entry.getValue();

                // Track header
                writer.println();
                writer.println(track.getName());
                addPatrolInfoHeadlineTo(writer);
                addScoreHeadlines(writer);

                // Patrol data
                int rank = 1;
                for (Patrol patrol : patrols) {
                    writer.printf("%d,%s,%s,%d,%d,%d%n",
                            rank++,
                            escapeCsv(patrol.getPatrolName()),
                            escapeCsv(patrol.getTroop()),
                            patrol.getTotalScorePoint(),
                            patrol.getTotalStylePoint(),
                            patrol.getTotalScore()
                    );
                }
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }


    public ByteArrayInputStream exportMultipleTracksToCsvComplete(Map<Track, List<Patrol>> results, List<Station> stations) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

            for (Map.Entry<Track, List<Patrol>> entry : results.entrySet()) {
                Track track = entry.getKey();
                List<Patrol> patrols = entry.getValue();

                // Track header
                writer.println();
                writer.println(track.getName());

                //Headers for scores
                addPatrolInfoHeadlineTo(writer);
                addHeadlinesFromStations(stations, writer);
                addScoreHeadlines(writer);

                // scores
                int rank = 1;
                for (Patrol patrol : patrols) {
                    writer.printf("%d,%s,%s,",
                            rank++,
                            escapeCsv(patrol.getPatrolName()),
                            escapeCsv(patrol.getTroop())
                    );

                    for (Station station : stations) {
                        writer.printf("%s", getScoreForPatrolOnStation(patrol, station));
                    }


                    writer.printf("%d,%d,%d%n",
                            patrol.getTotalScorePoint(),
                            patrol.getTotalStylePoint(),
                            patrol.getTotalScore()
                    );
                }
            }

            //Add station info
            writer.println();
            writer.println("Kontrollinformation");
            for (Station station : stations) {
                writer.printf("%d %s%n",
                        station.getStationNumber(),
                        station.getStationName());
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV", e);
        }


    }

    private String getScoreForPatrolOnStation(Patrol patrol, Station station) {
        StringBuilder stationScore = new StringBuilder();
        patrol.getScores().stream().filter(score ->
                        score.getStation().getStationNumber() == station.getStationNumber())
                .findFirst()
                .ifPresentOrElse(score -> {
                            if (Boolean.TRUE.equals(score.getStation().getWaypoint())) {
                                stationScore.append(printWaypoint(score.isVisitedWaypoint()));
                            } else {
                                stationScore.append(score.getScorePoint())
                                        .append(DELIMITER)
                                        .append(score.getStylePoint());
                            }
                            stationScore.append(DELIMITER);
                        },
                        () ->
                                addNoScoreTo(stationScore, station)
                );
        return stationScore.toString();

    }


    private void addPatrolInfoHeadlineTo(PrintWriter writer) {
        writer.print("Placering,Patrullnamn,Scoutkår,");
    }


    protected void addHeadlinesFromStations(List<Station> stations, PrintWriter writer) {

        for (Station station : stations) {
            if (Boolean.TRUE.equals(station.getWaypoint())) {
                writer.printf("Milstolpe %d,", station.getStationNumber());
            } else {
                writer.printf("K%d-poäng,", station.getStationNumber());

                writer.printf("K%d-stil,", station.getStationNumber());
            }
        }
    }

    private void addScoreHeadlines(PrintWriter writer) {
        writer.println("Poäng,Stilpäng,Totalt");
    }


    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String printWaypoint(boolean visitedWaypoint) {
        if (visitedWaypoint) {
            return "passerat";
        } else {
            return "";
        }
    }


    private void addNoScoreTo(StringBuilder row, Station station) {
        if (Boolean.TRUE.equals(station.getWaypoint())) {
            row.append(DELIMITER);
        } else {
            row.append(DELIMITER);
            row.append(DELIMITER);
        }
    }

}
