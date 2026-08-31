package se.scouttavling.gokapp.station;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.track.Track;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "station_seq")
    @SequenceGenerator(name = "station_seq", sequenceName = "STATION_SEQ", allocationSize = 5)
    private Integer id;

    @Column(name = "stationnumber", length = 4, nullable = false)
    private int stationNumber;

    @NotEmpty(message = "Fyll i ett namn på kontrollen")
    @Column(name = "stationname", length = 100)
    private String stationName;

    @Column(name = "alltracks")
    @Builder.Default
    private boolean allTracks = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "station_track")
    @Builder.Default
    private Set<Track> tracks = new HashSet<>();

    @Column(name = "minscore", length = 4)
    private int minScore;

    @Column(name = "maxscore", length = 4)
    private int maxScore;

    @Column(name = "minstylescore", length = 4)
    private int minStyleScore;

    @Column(name = "maxstylescore", length = 4)
    private int maxStyleScore;

    @Column(name = "stationcontact", length = 50)
    private String stationContact;

    @Column(name = "stationphone", length = 50)
    private String stationPhonenumber;

    @Column(name = "waypoint")
    @Builder.Default
    private Boolean waypoint = false;

    @Column(name = "usedistinctscores")
    @Builder.Default
    private Boolean useDistinctScores = false;

    @Column(name = "distinctscores", length = 500)
    private String distinctScores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User stationUser; // the user allowed to score this station

    @Transient
    public boolean isVisibleToTrack(Track track) {
        return allTracks || tracks.contains(track);
    }

    /**
     * The distinct/explicit list of allowed score values (used when useDistinctScores is true),
     * parsed from the comma-separated distinctScores string, deduped and sorted ascending.
     * Invalid/non-numeric tokens are silently ignored.
     */
    @Transient
    public List<Integer> getDistinctScoreList() {
        if (distinctScores == null || distinctScores.isBlank()) {
            return List.of();
        }
        return Arrays.stream(distinctScores.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Integer.valueOf(s);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * How many achievable values at this station rank above the given score: 0 for the
     * top score, 1 for the second-best, and so on. Used for the ranking tiebreaker cascade
     * (Patrol.getNumberOfXPoints) so range and distinct-score stations compare on equal
     * footing instead of assuming every station's values are consecutive integers.
     * Returns -1 if scoreValue isn't one of this station's distinct values (distinct mode only).
     */
    @Transient
    public int rankOfScore(int scoreValue) {
        if (Boolean.TRUE.equals(useDistinctScores)) {
            List<Integer> values = getDistinctScoreList();
            int idx = values.indexOf(scoreValue);
            return idx < 0 ? -1 : values.size() - 1 - idx;
        }
        return maxScore - scoreValue;
    }

    /**
     * Virtual selector used by the station edit form's radio group to drive the
     * mutually exclusive waypoint / useDistinctScores flags without adding a
     * separate persisted enum column (and the data migration that would require).
     */
    @Transient
    public String getMode() {
        if (Boolean.TRUE.equals(waypoint)) {
            return "WAYPOINT";
        }
        if (Boolean.TRUE.equals(useDistinctScores)) {
            return "DISTINCT";
        }
        return "RANGE";
    }

    @Transient
    public void setMode(String mode) {
        if (mode == null) {
            return;
        }
        switch (mode) {
            case "WAYPOINT" -> {
                waypoint = true;
                useDistinctScores = false;
            }
            case "DISTINCT" -> {
                waypoint = false;
                useDistinctScores = true;
            }
            default -> {
                waypoint = false;
                useDistinctScores = false;
            }
        }
    }
}
