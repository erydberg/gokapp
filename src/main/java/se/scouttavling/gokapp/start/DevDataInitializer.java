package se.scouttavling.gokapp.start;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
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

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
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
            createStation(1, "Livlina", 0, 10, 0, 1, "Erik", "12121212");
            createStation(2, "Sjukvård", 0, 10, 0, 1, "Erik", "12121212");
            createStation(3, "Knopar", 0, 10, 0, 1, "Erik", "12121212");
            createStation(4, "Hinderbana", 0, 10, 0, 1, "Erik", "12121212");
            createStation(5, "Kims spel", 0, 10, 0, 1, "Erik", "12121212");
            createStation(6, "Eldning", 0, 10, 0, 1, "Erik", "12121212");
            createStation(7, "Woodcraft", 0, 10, 0, 1, "Erik", "12121212");
            createStation(8, "Orientering", 0, 10, 0, 1, "Erik", "12121212");
            createStation(9, "Suduku", 0, 10, 0, 1, "Erik", "12121212");
            System.out.println("Done saving stations");

        }
    }

    private void initPatrols() {
        if (patrolService.getAllPatrols().isEmpty()) {
            System.out.println("saving patrols in test");

            Track trackFirst = trackService.findAllTracks().getFirst();
            Track trackLast = trackService.findAllTracks().getLast();

            Patrol p1 = createPatrol("Patrol 1 - track A", "Nisse", "nisse@mail.se", "12121212", Status.REGISTERED, "Scoutkår A", trackFirst);
            createScoreTo(p1, stationService.getAll().getFirst(), 8, 1);
            createScoreTo(p1, stationService.getAll().getLast(), 3, 0);
            createScoreTo(p1, stationService.getAll().get(2), 2, 1);

            Patrol p2 = createPatrol("Patrol 2 - track B", "Agneta", "agneta@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            createScoreTo(p2, stationService.getAll().getFirst(), 5, 0);
            createScoreTo(p2, stationService.getAll().getLast(), 4, 1);
            createScoreTo(p2, stationService.getAll().get(2), 10, 1);

            Patrol p3 = createPatrol("Patrol 3 - track A", "Beata", "beataagneta@mail.se", "3331552", Status.ACTIVE, "Scoutkår C", trackLast);
            createScoreTo(p3, stationService.getAll().getFirst(), 1, 0);
            createScoreTo(p3, stationService.getAll().getLast(), 7, 1);
            createScoreTo(p3, stationService.getAll().get(2), 4, 1);

            Patrol p4 = createPatrol("Patrol 4", "Wilmer", "WA@mail.se", "631212", Status.REGISTERED, "Scoutkår A", trackLast);
            createScoreTo(p4, stationService.getAll().getFirst(), 5, 0);
            createScoreTo(p4, stationService.getAll().getLast(), 4, 1);
            createScoreTo(p4, stationService.getAll().get(2), 10, 1);

            Patrol p5 = createPatrol("Patrol 5 - on the move", "Stina", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            createScoreTo(p5, stationService.getAll().getFirst(), 5, 0);
            createScoreTo(p5, stationService.getAll().getLast(), 7, 1);
            createScoreTo(p5, stationService.getAll().get(2), 7, 1);

            Patrol p6 = createPatrol("Patrol 6 - denna patrull har ett väldigt långt namn så får vi se hur det blir", "Hedvig", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            Patrol p7 = createPatrol("Patrol 7 - no points", "Hedvig", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            Patrol p8 = createPatrol("Patrol 8 - no points", "Hedvig", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            Patrol p9 = createPatrol("Patrol 9 - no points", "Hedvig", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
            Patrol p10 = createPatrol("Patrol 10 - no points", "Hedvig", "stina@mail.se", "3331212", Status.REGISTERED, "Scoutkår A", trackLast);
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

    private void createStation(int stationNumber, String stationName, int minScore, int maxScore, int minStyleScore, int maxStyleScore, String contact, String mobile) {
        Station station = new Station();
        station.setStationName(stationName);
        station.setStationNumber(stationNumber);
        station.setMinScore(minScore);
        station.setMaxScore(maxScore);
        station.setMinStyleScore(minStyleScore);
        station.setMaxStyleScore(maxStyleScore);
        station.setStationContact(contact);
        station.setStationPhonenumber(mobile);
        stationService.save(station);
        System.out.println("Saved station " + stationName);
    }


    public Patrol createPatrol(String name, String contact, String mail, String phone, Status status, String troop, Track track) {

        Patrol patrol = new Patrol();
        patrol.setPatrolName(name);
        patrol.setTrack(track);
        patrol.setLeaderContact(contact);
        patrol.setLeaderContactMail(mail);
        patrol.setLeaderContactPhone(phone);
        patrol.setStatus(status);
        patrol.setTroop(troop);
        patrolService.save(patrol);
        System.out.println("Saved patrol " + name);
        return patrol;
    }


    private void createScoreTo(Patrol patrol, Station station, int scorePoint, int styleScore) {
        Score score = Score.builder().patrol(patrol).station(station).scorePoint(scorePoint).stylePoint(styleScore).build();
        scoreService.save(score);
    }
}

