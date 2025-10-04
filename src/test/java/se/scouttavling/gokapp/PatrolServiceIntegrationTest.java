package se.scouttavling.gokapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolRepository;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationRepository;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PatrolServiceIntegrationTest {

    @Autowired
    private PatrolService patrolService;

    @Autowired
    private PatrolRepository patrolRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private StationRepository stationRepository;

    private Track testTrack;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        patrolRepository.deleteAll();
        trackRepository.deleteAll();
        stationRepository.deleteAll();

        // Create a test track
        testTrack = new Track();
        testTrack.setName("Test Track");
        trackRepository.save(testTrack);

        Station station = new Station();
        station.setStationNumber(1);
        station.setStationName("Test Station");
        stationRepository.save(station);
    }

    @Test
    void testSaveAndRetrievePatrol() {
        Patrol patrol = new Patrol();
        patrol.setPatrolName("Alpha Patrol");
        patrol.setTroop("Troop 1");
        patrol.setTrack(testTrack);
        patrol.setLeaderContact("kalle");
        patrol.setLeaderContactMail("mail@mail.se");
        patrol.setLeaderContactPhone("3333333");

        Patrol saved = patrolService.save(patrol);
        assertThat(saved.getPatrolId()).isNotNull();

        List<Patrol> allPatrols = patrolService.getAllPatrols();
        assertThat(allPatrols).hasSize(1);
        assertThat(allPatrols.get(0).getPatrolName()).isEqualTo("Alpha Patrol");
    }

    @Test
    void testGetPatrolById() {
        Patrol patrol = new Patrol();
        patrol.setPatrolName("Bravo Patrol");
        patrol.setTroop("Troop 2");
        patrol.setLeaderContact("kalle");
        patrol.setLeaderContactMail("mail@mail.se");
        patrol.setLeaderContactPhone("3333333");
        patrol.setTrack(testTrack);
        Patrol saved = patrolService.save(patrol);

        Patrol found = patrolService.getPatrolById(saved.getPatrolId()).orElseThrow();
        assertThat(found.getPatrolName()).isEqualTo("Bravo Patrol");
    }

    @Test
    void testGetAllPatrolsByTrack() {
        Patrol patrol1 = new Patrol();
        patrol1.setPatrolName("Patrol 1");
        patrol1.setLeaderContact("kalle");
        patrol1.setLeaderContactMail("mail@mail.se");
        patrol1.setLeaderContactPhone("3333333");
        patrol1.setTroop("Troop 1");
        patrol1.setTrack(testTrack);
        patrolService.save(patrol1);

        Patrol patrol2 = new Patrol();
        patrol2.setPatrolName("Patrol 2");
        patrol2.setTroop("Troop 2");
        patrol2.setLeaderContact("nissa");
        patrol2.setLeaderContactMail("mailen@t.se");
        patrol2.setLeaderContactPhone("34343434");
        patrol2.setTrack(testTrack);
        patrolService.save(patrol2);

        List<Patrol> patrols = patrolService.getAllPatrolsByTrackSortedByScore(testTrack);
        assertThat(patrols).hasSize(2);
        assertThat(patrols.get(0).getTotalScore()).isZero(); // initial score
    }
}

