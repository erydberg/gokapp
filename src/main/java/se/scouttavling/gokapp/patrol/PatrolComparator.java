package se.scouttavling.gokapp.patrol;

import se.scouttavling.gokapp.score.Score;

import java.time.LocalDateTime;
import java.util.Comparator;

public class PatrolComparator {


    // Sort by patrol name
    public static final Comparator<Patrol> BY_NAME =
            Comparator.comparing(Patrol::getPatrolName, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by score (descending, higher first)
    public static final Comparator<Patrol> BY_SCORE =
            Comparator.comparingInt(Patrol::getTotalScore).reversed();

    // Sort by track name
    public static final Comparator<Patrol> BY_TRACK =
            Comparator.comparing(p -> p.getTrack().getName(), Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by start station
    public static final Comparator<Patrol> BY_STARTSTATION =
            Comparator.comparing(p -> p.getStartStation().getStationName(), Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by troop
    public static final Comparator<Patrol> BY_TROOP =
            Comparator.comparing(Patrol::getTroop, Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by status
    public static final Comparator<Patrol> BY_STATUS =
            Comparator.comparing(p -> p.getStatus().name(), Comparator.nullsLast(String::compareToIgnoreCase));

    // Sort by latestScore
    public static final Comparator<Patrol> BY_LATEST_SCORE =
            Comparator.comparing((Patrol p) -> {
                Score score = p.getLatestScore();
                return score != null ? score.getLastSaved() : null;
            }, Comparator.nullsLast(LocalDateTime::compareTo));
}
