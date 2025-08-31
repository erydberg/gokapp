package se.scouttavling.gokapp.patrol;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.score.Score;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "patrol")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"scores"})
public class Patrol implements Comparable<Patrol> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patrolSeqGen")
    @SequenceGenerator(name = "patrolSeqGen", sequenceName = "PATROL_SEQ", initialValue = 100, allocationSize = 5)
    @Column(name = "patrolid", nullable = false)
    private Integer patrolId;

    @Column(name = "externalid")
    private String externalId;

    @NotEmpty(message = "Fyll i ett namn på patrullen")
    @Column(name = "patrolname", length = 120)
    private String patrolName;

    @NotEmpty(message = "Fyll i patrullens scoutkår")
    @Column(name = "troop", length = 100)
    private String troop;

    @NotNull(message = "Välj en klass")
    @ManyToOne
    @JoinColumn(name = "fk_track")
    private Track track;

    @Column(name = "starttime", length = 10)
    private String startTime;

    @Column(name = "endtime", length = 10)
    private String endTime;

    @Column(name = "members", length = 500)
    private String members;

    @Column(name = "note", length = 500)
    private String note;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("lastSaved desc")
    @JoinColumn(name = "fk_patrol")
    private Set<Score> scores = new LinkedHashSet<>();

    @NotEmpty(message = "Missa inte att fylla i kontaktperson")
    @Column(name = "leadercontact", length = 100)
    private String leaderContact;

    @NotEmpty(message = "E-postadress krävs")
    @Email(message = "Se till att e-postadressen är korrekt")
    @Column(name = "contactmail", length = 250)
    private String leaderContactMail;

    @NotEmpty(message = "Ett telefonnummer vill vi ha också")
    @Column(name = "leadercontactphone", length = 100)
    private String leaderContactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "date_registered")
    private LocalDateTime dateRegistered;

    @Column(name = "paid")
    private Boolean paid = false;

    @ManyToOne
    @JoinColumn(name = "fk_station")
    private Station startStation;


    /* === Derived / helper methods === */
    @Transient
    public String getPatrolInfo() {
        return patrolName + " (" + patrolId + ") - "
                + (track != null ? track.getName() : "(odefinierad klass)") + " (" + troop + ")";
    }

    @Transient
    public Integer getTotalScorePoint() {
        return scores.stream()
                .mapToInt(Score::getScorePoint)
                .sum();
    }

    @Transient
    public Integer getTotalStylePoint() {
        return scores.stream()
                .mapToInt(Score::getStylePoint)
                .sum();
    }

    @Transient
    public Integer getTotalReportedStations() {
        return scores.size();
    }

    @Transient
    public Integer getTotalScore() {
        return getTotalStylePoint() + getTotalScorePoint();
    }

    @Transient
    public Score getLatestScore() {
        return scores.stream()
                .max(Comparator.comparing(Score::getLastSaved, Comparator.nullsFirst(LocalDateTime::compareTo)))
                .orElse(null);
    }

    @Transient
    public Integer getNumberOfMaxPoints() {
        return getNumberOfXPoints(0);
    }

    @Transient
    public Integer getNumberOfXPoints(int x) {
        return (int) scores.stream()
                .filter(score -> {
                    Station currentStation = score.getStation();
                    if (currentStation == null) return false;
                    if (currentStation.getWaypoint() == null) {
                        currentStation.setWaypoint(false);
                    }
                    int maxScoreOnStation = currentStation.getMaxScore() - x;
                    return !currentStation.getWaypoint() && maxScoreOnStation > 0
                            && score.getScorePoint() == maxScoreOnStation;
                })
                .count();
    }

    @Override
    public int compareTo(Patrol p) {
        int comp = p.getTotalScore().compareTo(getTotalScore());
        if (comp == 0) {
            comp = p.getTotalScorePoint().compareTo(getTotalScorePoint());
        }
        if (comp == 0) {
            for (int i = 0; i <= 50; i++) {
                comp = p.getNumberOfXPoints(i).compareTo(getNumberOfXPoints(i));
                if (comp != 0) {
                    break;
                }
            }
        }
        return comp;
    }
}

