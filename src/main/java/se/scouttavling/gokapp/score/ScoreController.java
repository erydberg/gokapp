package se.scouttavling.gokapp.score;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.security.Role;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationSelectionForm;
import se.scouttavling.gokapp.station.StationService;


import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {

    private final PatrolService patrolService;
    private final StationService stationService;
    private final ScoreService scoreService;
    private final ConfigService configService;
    private final UserService userService;


    @ModelAttribute("config")
    public Config loadConfig() {
        return configService.getCurrentConfig();
    }

    /**
     * Start page for adding a score
     * loads the station list
     */
    @GetMapping
    public String startScore(Principal principal, Model model) {
        System.out.println("user " + principal.getName());
        Optional<User> currentUser = userService.findUserByUsername(principal.getName());
        List<Station> stations;
        if (currentUser.isPresent()) {
            if (currentUser.get().getRoles().contains(Role.ROLE_ADMIN)) {
                stations = stationService.getAll();
            } else {
                stations = stationService.getForUser(currentUser.get());
            }
        } else {
            stations = Collections.emptyList();
        }

        if (stations.size() == 1) {
            return "redirect:/score/selectstation?stationId=" + stations.getFirst().getId();
        }

        model.addAttribute("stationSelectionForm", new StationSelectionForm(null));
        model.addAttribute("stations", stations);

        return "score_select_station";
    }

    @GetMapping("/selectstation")
    public String selectStationGet(@RequestParam Integer stationId, Model model) {
        return loadStationPage(stationId, model);
    }

    @PostMapping("/selectstation")
    public String selectStationPost(@ModelAttribute("stationSelectionForm") StationSelectionForm form,
                                    BindingResult result,
                                    Model model) {
        if(form.stationId() == null) {
            model.addAttribute("errormsg", "Du måste välja en kontroll");
            model.addAttribute("stations", stationService.getAll());
            return "score_select_station";
        }

        return loadStationPage(form.stationId(), model);
    }

    private String loadStationPage(Integer stationId, Model model) {
        Station station = stationService.getStationById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        Score score = Score.builder().station(station).build();
        model.addAttribute("score", score);
        model.addAttribute("station", station);
        model.addAttribute("patrols", patrolService.allPatrolsLeftOnStation(station.getId()));
        return "score_report";
    }


    /**
     * Save a score
     */
    @PostMapping()
    public String saveScore(@ModelAttribute("score") Score score,
                            BindingResult result,
                            Model model) {

        Station station = stationService.getStationById(score.getStation().getId()).orElseThrow(() -> new IllegalArgumentException("Station not found"));

        if (score.getPatrol() == null) {
            model.addAttribute("station", station);
            model.addAttribute("errormsg", "Du måste välja en patrull innan du sparar poängen.");
            score.setStation(station);
            model.addAttribute("score", score);
            List<Patrol> patrols = patrolService.allPatrolsLeftOnStation(station.getId());
            model.addAttribute("patrols", patrols);

            return "score_report";
        }

        scoreService.save(score);

        Patrol patrol = patrolService.getPatrolById(score.getPatrol().getPatrolId()).orElseThrow(() -> new IllegalArgumentException("Patrol not found"));

        if (score.isVisitedWaypoint()) {
            model.addAttribute("alertmsg",
                    "Sparat att patrull " + score.getPatrol().getPatrolName() + " har passerat kontrollen.");
        } else {
            model.addAttribute("alertmsg",
                    "Sparat " + score.getScorePoint() + " poäng och " + score.getStylePoint() +
                            " stilpoäng till " + patrol.getPatrolName() +
                            " från " + patrol.getTroop() + ".");
        }

        // Reset form for new input
        Score newScore = new Score();
        newScore.setStation(station);
        List<Patrol> patrols = patrolService.allPatrolsLeftOnStation(station.getId());

        model.addAttribute("station", station);
        model.addAttribute("score", newScore);
        model.addAttribute("patrols", patrols);

        System.out.println("no of saved scores: " + scoreService.findAll().size());
        return "score_report";
    }
}

