package se.scouttavling.gokapp.score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.scouttavling.gokapp.GokappApplication;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolRepository;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationRepository;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ScoreService.save is the single choke point all three score-entry controllers go through -
 * a tampered request can submit any scorePoint/stylePoint regardless of what the UI's radio
 * buttons would offer, so this is where server-side validation has to live.
 */
@SpringBootTest(classes = GokappApplication.class)
@ActiveProfiles("test")
class ScoreServiceValidationTest {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PatrolRepository patrolRepository;

    @Autowired
    private TrackRepository trackRepository;

    private Patrol patrol;

    @BeforeEach
    void setUp() {
        patrolRepository.deleteAll();
        stationRepository.deleteAll();

        Track track = new Track();
        track.setName("Test Track");
        trackRepository.save(track);

        patrol = new Patrol();
        patrol.setPatrolName("Test Patrol");
        patrol.setTroop("Test Troop");
        patrol.setTrack(track);
        patrol.setLeaderContact("Leader");
        patrol.setLeaderContactMail("leader@example.com");
        patrol.setLeaderContactPhone("111111111");
        patrolRepository.save(patrol);
    }

    @Test
    void acceptsScoreWithinRangeStationsMinMax() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Range").stationNumber(1).minScore(0).maxScore(10).minStyleScore(0).maxStyleScore(1).build());

        Score saved = scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(7).stylePoint(1).build());

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void rejectsScoreAboveRangeStationsMax() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Range").stationNumber(1).minScore(0).maxScore(10).build());

        assertThatThrownBy(() ->
                scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(11).build()))
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    void rejectsScoreBelowRangeStationsMin() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Range").stationNumber(1).minScore(2).maxScore(10).build());

        assertThatThrownBy(() ->
                scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(1).build()))
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    void acceptsScoreThatIsOneOfTheDistinctStationsValues() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Distinct").stationNumber(1).useDistinctScores(true).distinctScores("0,5,10,15,20").maxScore(20).build());

        Score saved = scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(15).build());

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void rejectsScoreNotInDistinctStationsValueList() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Distinct").stationNumber(1).useDistinctScores(true).distinctScores("0,5,10,15,20").maxScore(20).build());

        // 12 isn't one of the station's allowed values, even though it's inside the 0-20 span.
        assertThatThrownBy(() ->
                scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(12).build()))
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    void acceptsZeroScoreForWaypointStation() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Waypoint").stationNumber(1).waypoint(true).build());

        Score saved = scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(0).visitedWaypoint(true).build());

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void rejectsNonZeroScoreForWaypointStation() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Waypoint").stationNumber(1).waypoint(true).build());

        assertThatThrownBy(() ->
                scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(5).build()))
                .isInstanceOf(InvalidScoreException.class);
    }

    @Test
    void rejectsStylePointOutsideStationsMinMax() {
        Station station = stationRepository.save(Station.builder()
                .stationName("Range").stationNumber(1).minScore(0).maxScore(10).minStyleScore(0).maxStyleScore(1).build());

        assertThatThrownBy(() ->
                scoreService.save(Score.builder().station(station).patrol(patrol).scorePoint(5).stylePoint(2).build()))
                .isInstanceOf(InvalidScoreException.class);
    }
}
