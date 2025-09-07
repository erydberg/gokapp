package se.scouttavling.gokapp.score;

import jakarta.persistence.*;
import lombok.*;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.station.Station;

import java.time.LocalDateTime;

@Entity
@Table(name = "score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patrol_id", nullable = false)
    private Patrol patrol;

    private int scorePoint;

    private int stylePoint;

    private boolean visitedWaypoint;

    private LocalDateTime lastSaved;
}

