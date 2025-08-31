package se.scouttavling.gokapp.patrol;

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

    // Sort by troop
    public static final Comparator<Patrol> BY_TROOP =
            Comparator.comparing(Patrol::getTroop, Comparator.nullsLast(String::compareToIgnoreCase));
}
