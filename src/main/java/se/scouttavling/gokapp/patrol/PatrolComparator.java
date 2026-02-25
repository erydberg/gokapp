package se.scouttavling.gokapp.patrol;

import se.scouttavling.gokapp.score.Score;

import java.time.LocalDateTime;
import java.util.Comparator;

public class PatrolComparator {


    // Sort by patrol name
    public static final Comparator<Patrol> BY_NAME =
            Comparator.comparing(p -> p.getPatrolName() != null ? p.getPatrolName() : null, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by score (descending, higher first)
    public static final Comparator<Patrol> BY_SCORE =
            Comparator.comparingInt(Patrol::getTotalScore).reversed();

    // Sort by track name
    public static final Comparator<Patrol> BY_TRACK =
            Comparator.comparing(p -> p.getTrack() != null ? p.getTrack().getName() : null, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by start station
    public static final Comparator<Patrol> BY_STARTSTATION =
            Comparator.comparing(p -> p.getStartStation() != null ? p.getStartStation().getStationName() : null, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by number of scores ie. visited stations
    public static final Comparator<Patrol> BY_NO_OF_STATIONS =
            Comparator.comparing(p -> p.getScores() != null ? p.getScores().size() : null, Comparator.nullsLast(Integer::compareTo));

    // Sort by troop
    public static final Comparator<Patrol> BY_TROOP =
            Comparator.comparing(p -> p.getTroop() != null ? p.getTroop() : null, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by status
    public static final Comparator<Patrol> BY_STATUS =
            Comparator.comparing(p -> p.getStatus() != null ? p.getStatus().name() : null, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by latestScore
    public static final Comparator<Patrol> BY_LATEST_SCORE =
            Comparator.comparing((Patrol p) -> {
                Score score = p.getLatestScore();
                return score != null ? score.getLastSaved() : null;
            }, Comparator.nullsLast(LocalDateTime::compareTo));


}
