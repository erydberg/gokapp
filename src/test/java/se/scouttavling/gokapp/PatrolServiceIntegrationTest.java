package se.scouttavling.gokapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolRepository;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.patrol.Status;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.score.ScoreService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationRepository;
import se.scouttavling.gokapp.station.StationService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackRepository;
import se.scouttavling.gokapp.track.TrackService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PatrolServiceIntegrationTest {

    @Autowired
    private PatrolService patrolService;

    @Autowired
    private PatrolRepository patrolRepository;

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationService stationService;

    @Autowired
    private ScoreService scoreService;


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

    @Test
    void testGetPatrolsLeftOnStation() {
        System.out.println("Starting test");
        Station station = Station.builder().stationNumber(10).stationName("test 10").allTracks(true).minScore(0).maxScore(10).minStyleScore(0).maxStyleScore(1).stationContact("erik").stationPhonenumber("121212").build();
        stationService.save(station);

        Station station2 = Station.builder().stationNumber(11).stationName("test 111").allTracks(true).minScore(0).maxScore(10).minStyleScore(0).maxStyleScore(1).stationContact("erik").stationPhonenumber("121212").build();
        stationService.save(station2);

        Track track1 = Track.builder().name("Spårare").build();
        trackService.saveTrack(track1);

        Track track2 = Track.builder().name("Upptäckare").build();
        trackService.saveTrack(track2);

        Set<Track> tracksForStation = new HashSet<>();
        tracksForStation.add(track1);

        //Station 3 has only track spårare
        Station station3 = Station.builder().stationNumber(13).stationName("test only spårare").allTracks(false).tracks(tracksForStation).minScore(0).maxScore(10).minStyleScore(0).maxStyleScore(1).stationContact("erik").stationPhonenumber("121212").build();
        stationService.save(station3);

        Patrol patrol1 = Patrol.builder().patrolName("patrull1").status(Status.ACTIVE).troop("scoutkåren").track(track1).leaderContact("erik").leaderContactMail("erik@mail.se").leaderContactPhone("232323").build();
        patrolService.save(patrol1);

        Patrol patrol2 = Patrol.builder().patrolName("patrull2").status(Status.ACTIVE).troop("scoutkåren").track(track2).leaderContact("erik").leaderContactMail("erik@mail.se").leaderContactPhone("232323").build();
        patrolService.save(patrol2);

        Score score = Score.builder().stylePoint(0).scorePoint(10).patrol(patrol1).station(station).build();
        scoreService.save(score);

        //Station 1 - one patrol has got score
        assertThat(patrolService.allPatrolsLeftOnStation(station.getId()).size()).isEqualTo(1);
        //Station 2 - no patrol has scores
        assertThat(patrolService.allPatrolsLeftOnStation(station2.getId()).size()).isEqualTo(2);
        //Station 3 - only for track1
        assertThat(patrolService.allPatrolsLeftOnStation(station3.getId()).size()).isEqualTo(1);
    }
}

