package se.scouttavling.gokapp.score;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.station.Station;

@Entity
@Table(name = "score")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {

    @Id
    @SequenceGenerator(name = "scoreSeqGen", sequenceName = "SCORE_SEQ", initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "scoreSeqGen")
    @Column(name = "scoreid", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_station")
    private Station station;

    @Column(name = "scorepoint")
    private int scorePoint;

    @Column(name = "stylepoint")
    private int stylePoint;

    @ManyToOne
    @JoinColumn(name = "fk_patrol")
    private Patrol patrol;

    @Column(name = "last_saved")
    private LocalDateTime lastSaved;

    @Column(name = "visited_waypoint")
    private boolean visitedWaypoint;
}

