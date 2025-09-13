package se.scouttavling.gokapp.start;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.patrol.Status;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.score.ScoreService;
import se.scouttavling.gokapp.security.Role;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

@Component
@RequiredArgsConstructor
@Profile("dev") // Only active in the "dev" profile
public class DevDataInitializer {

    private final StationService stationService;
    private final TrackService trackService;
    private final PatrolService patrolService;
    private final UserService userService;
    private final ScoreService scoreService;

    @PostConstruct
    public void init() {
        initTracks();
        initStations();
        initPatrols();
        initUsers();
    }

    private void initTracks() {
        System.out.println("initTracks in dev");
        if (trackService.findAllTracks().isEmpty()) {
            Track t1 = new Track();
            t1.setName("Track A");
            trackService.saveTrack(t1);
            System.out.println("saved track 1");

            Track t2 = new Track();
            t2.setName("Track B");
            trackService.saveTrack(t2);
            System.out.println("saved track 2");
        }
    }

    private void initStations() {
        if (stationService.getAll().isEmpty()) {
            System.out.println("Save stations");
            Station s1 = new Station();
            s1.setStationName("Station 1");
            s1.setStationNumber(1);
            s1.setMinScore(0);
            s1.setMaxScore(10);
            s1.setMinStyleScore(0);
            s1.setMaxStyleScore(1);
            s1.setStationContact("kalle kula");
            s1.setStationPhonenumber("031-2222222");
            stationService.save(s1);
            System.out.println("Saved station 1");

            Station s2 = new Station();
            s2.setStationName("Station 2");
            s2.setStationNumber(1);
            s2.setMinScore(0);
            s2.setMaxScore(10);
            s2.setMinStyleScore(0);
            s2.setMaxStyleScore(1);
            s2.setStationContact("stina");
            s2.setStationPhonenumber("011-111112");
            stationService.save(s2);
            System.out.println("Saved station 2");
        }
    }

    private void initPatrols() {
        if (patrolService.getAllPatrols().isEmpty()) {
            System.out.println("saving patrols in test");
            Patrol p1 = new Patrol();
            p1.setPatrolName("Patrol 1 - track A");
            p1.setTrack(trackService.findAllTracks().getFirst());
            p1.setLeaderContact("Nisse");
            p1.setLeaderContactMail("nisse@mail.se");
            p1.setLeaderContactPhone("444444");
            p1.setStatus(Status.REGISTERED);
            p1.setTroop("Scoutkåren");
            patrolService.save(p1);
            System.out.println("Saved patrol 1");

            Score score = Score.builder().patrol(p1).station(stationService.getAll().getFirst()).scorePoint(8).stylePoint(1).build();
            scoreService.save(score);
            Score score2 = Score.builder().patrol(p1).station(stationService.getAll().getLast()).scorePoint(4).stylePoint(0).build();
            scoreService.save(score2);

            Patrol p2 = new Patrol();
            p2.setPatrolName("Patrol 2");
            p2.setTrack(trackService.findAllTracks().getFirst());
            p2.setLeaderContact("Mosse");
            p2.setLeaderContactMail("mosse@mail.se");
            p2.setLeaderContactPhone("3333");
            p2.setStatus(Status.REGISTERED);
            p2.setTroop("Scoutkåren B");
            patrolService.save(p2);
            System.out.println("Saved patrol 1");

            Score score3 = Score.builder().patrol(p2).station(stationService.getAll().getFirst()).scorePoint(7).stylePoint(1).build();
            scoreService.save(score3);
            Score score4 = Score.builder().patrol(p2).station(stationService.getAll().getLast()).scorePoint(4).stylePoint(1).build();
            scoreService.save(score4);

            Patrol p3 = new Patrol();
            p3.setPatrolName("Patrol 3 - track A");
            p3.setTrack(trackService.findAllTracks().getFirst());
            p3.setLeaderContact("Abraham");
            p3.setLeaderContactMail("aa@mail.se");
            p3.setLeaderContactPhone("77777");
            p3.setStatus(Status.REGISTERED);
            p3.setTroop("Scoutkåren B");
            patrolService.save(p3);
            System.out.println("Saved patrol 3");

            Score score5 = Score.builder().patrol(p3).station(stationService.getAll().getFirst()).scorePoint(10).stylePoint(1).build();
            scoreService.save(score5);
            Score score6 = Score.builder().patrol(p3).station(stationService.getAll().getLast()).scorePoint(2).stylePoint(1).build();
            scoreService.save(score6);

            Patrol p4 = new Patrol();
            p4.setPatrolName("Patrol 4 utan poäng");
            p4.setTrack(trackService.findAllTracks().getFirst());
            p4.setLeaderContact("mossa");
            p4.setLeaderContactMail("bb@mail.se");
            p4.setLeaderContactPhone("73337");
            p4.setStatus(Status.REGISTERED);
            p4.setTroop("Scoutkåren B");
            patrolService.save(p4);
            System.out.println("Saved patrol 4");

            Patrol p5 = new Patrol();
            p5.setPatrolName("Patrol 5 utan poäng");
            p5.setTrack(trackService.findAllTracks().getFirst());
            p5.setLeaderContact("rico");
            p5.setLeaderContactMail("bccc@mail.se");
            p5.setLeaderContactPhone("733334444437");
            p5.setStatus(Status.REGISTERED);
            p5.setTroop("Scoutkåren B");
            patrolService.save(p5);
            System.out.println("Saved patrol 5");
        }
    }



    private void initUsers() {
        User user = new User();
        user.setUsername("k1");
        user.setPassword("losen_test"); // Make sure UserService encodes the password
        user.addRole(Role.ROLE_USER);
        user.setEnabled(true);
        userService.save(user);
        System.out.println("User created: username=k1, password=losen_test");
    }
}

