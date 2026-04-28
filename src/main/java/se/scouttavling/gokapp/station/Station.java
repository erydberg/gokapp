package se.scouttavling.gokapp.station;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.track.Track;

import java.util.HashSet;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User stationUser; // the user allowed to score this station

    @Transient
    public boolean isVisibleToTrack(Track track) {
        return allTracks || tracks.contains(track);
    }
}
