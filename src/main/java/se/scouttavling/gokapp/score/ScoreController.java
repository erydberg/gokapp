package se.scouttavling.gokapp.score;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationSelectionForm;
import se.scouttavling.gokapp.station.StationService;


import java.util.List;

@Controller
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {

    private final PatrolService patrolService;
    private final StationService stationService;
    private final ScoreService scoreService;

    /**
     * Start page for adding a score
     * loads the station list
     */
    @GetMapping
    public String startScore(Model model) {
        model.addAttribute("stationSelectionForm", new StationSelectionForm(null));
        model.addAttribute("stations", stationService.getAll()); //TODO based on access
        return "score_select_station";
    }

    /**
     * Select station, then show patrols left on this station
     */
    @PostMapping("/selectstation")
    public String selectStation(@ModelAttribute("stationSelectionForm") StationSelectionForm form,
                                BindingResult result,
                                Model model) {

        if(form.stationId() == null) {
            model.addAttribute("errormsg", "Du måste välja en kontroll");
            model.addAttribute("stations", stationService.getAll());
            return "score_select_station";
        }

        Station station = stationService.getStationById(form.stationId()).orElseThrow(() -> new IllegalArgumentException("Station not found"));
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
            model.addAttribute("station",station);
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
        System.out.println("Station on new score is " + newScore.getStation().getStationName());
        List<Patrol> patrols = patrolService.allPatrolsLeftOnStation(station.getId());

        model.addAttribute("station", station);
        model.addAttribute("score", newScore);
        model.addAttribute("patrols", patrols);

        System.out.println("no of saved scores: " + scoreService.findAll().size());
        return "score_report";
    }


    /**
     * Edit an existing score
     */
    @GetMapping("/edit/{id}")
    public String editScore(@PathVariable Integer id, Model model) {
        Score score = scoreService.findByIdWithPatrolAndStation(id)
                .orElseThrow(() -> new IllegalArgumentException("Score not found"));
        model.addAttribute("score", score);
        model.addAttribute("stations", stationService.getAll());
        return "editscore";
    }


    /**
     * Delete a score
     */
    @PostMapping("/delete/{id}")
    public String deleteScore(@PathVariable Integer id, @RequestParam Integer patrolId, Model model) {
        scoreService.deleteById(id);

        Patrol patrol = patrolService.getPatrolById(patrolId)
                .orElseThrow(() -> new IllegalArgumentException("Patrol not found"));
        model.addAttribute("patrol", patrol);
        model.addAttribute("backurl", "/reports/patrols");
        return "viewpatrol";
    }
}

