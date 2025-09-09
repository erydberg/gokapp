package se.scouttavling.gokapp.station;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import se.scouttavling.gokapp.security.User;

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
    private Boolean waypoint = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User stationUser; // the user allowed to score this station
}
