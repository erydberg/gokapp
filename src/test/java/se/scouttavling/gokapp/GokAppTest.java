package se.scouttavling.gokapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolRepository;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationRepository;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = GokappApplication.class)
@ActiveProfiles("test")
public class GokAppTest {

    @Autowired
    private PatrolService patrolService;

    @Autowired
    private PatrolRepository patrolRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private StationRepository stationRepository;

    private Track testTrack;
    private Station station1;

    @BeforeEach
    void setUp() {
        patrolRepository.deleteAll();
        trackRepository.deleteAll();
        stationRepository.deleteAll();

        // Create a test track
        testTrack = new Track();
        testTrack.setName("Test Track");
        trackRepository.save(testTrack);

        // Create a station
        station1 = new Station();
        station1.setStationName("Station 1");
        station1.setStationNumber(1);
        stationRepository.save(station1);
    }

    @Test
    void testPatrolScoresAffectSorting() {
        // Patrol with higher totalScore
        Patrol highScorePatrol = new Patrol();
        highScorePatrol.setPatrolName("HighScore Patrol");
        highScorePatrol.setTroop("Troop 1");
        highScorePatrol.setTrack(testTrack);
        highScorePatrol.setLeaderContact("Alice");
        highScorePatrol.setLeaderContactMail("alice@example.com");
        highScorePatrol.setLeaderContactPhone("111111111");

        Score score1 = new Score();
        score1.setScorePoint(10);
        score1.setStylePoint(5);
        score1.setStation(station1);
        score1.setPatrol(highScorePatrol);

        highScorePatrol.getScores().add(score1);
        patrolService.save(highScorePatrol);

        // Patrol with lower totalScore
        Patrol lowScorePatrol = new Patrol();
        lowScorePatrol.setPatrolName("LowScore Patrol");
        lowScorePatrol.setTroop("Troop 2");
        lowScorePatrol.setTrack(testTrack);
        lowScorePatrol.setLeaderContact("Bob");
        lowScorePatrol.setLeaderContactMail("bob@example.com");
        lowScorePatrol.setLeaderContactPhone("222222222");

        Score score2 = new Score();
        score2.setScorePoint(3);
        score2.setStylePoint(2);
        score2.setStation(station1);
        score2.setPatrol(lowScorePatrol);

        lowScorePatrol.getScores().add(score2);
        patrolService.save(lowScorePatrol);

        // Retrieve and verify sorting by totalScore descending
        List<Patrol> sortedPatrols = patrolService.getAllPatrolsByTrack(testTrack);

        assertThat(sortedPatrols).hasSize(2);
        assertThat(sortedPatrols.get(0).getPatrolName()).isEqualTo("HighScore Patrol");
        assertThat(sortedPatrols.get(1).getPatrolName()).isEqualTo("LowScore Patrol");
    }

    @Test
    void testPatrolWithMultipleScores() {
        Patrol patrol = new Patrol();
        patrol.setPatrolName("MultiScore Patrol");
        patrol.setTroop("Troop 3");
        patrol.setTrack(testTrack);
        patrol.setLeaderContact("Charlie");
        patrol.setLeaderContactMail("charlie@example.com");
        patrol.setLeaderContactPhone("333333333");

        // Add multiple scores
        for (int i = 1; i <= 3; i++) {
            Score score = new Score();
            score.setScorePoint(i * 2);
            score.setStylePoint(i);
            score.setStation(station1);
            score.setPatrol(patrol);
            patrol.getScores().add(score);
        }

        patrolService.save(patrol);

        Optional<Patrol> retrieved = patrolService.getPatrolById(patrol.getPatrolId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTotalScore()).isEqualTo(2 + 4 + 6 + 1 + 2 + 3); // totalScorePoint + totalStylePoint
        assertThat(retrieved.get().getScores()).hasSize(3);
    }
}
