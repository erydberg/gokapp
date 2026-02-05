package se.scouttavling.gokapp.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
@RequestMapping("/correct")
@RequiredArgsConstructor
public class CorrectScoreController {

    private final StationService stationService;
    private final ScoreService scoreService;
    private final PatrolService patrolService;
    private final UserService userService;

    @GetMapping
    public String startCorrectScore(Principal principal, Model model) {
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
            return "redirect:/correct/selectstation?stationId=" + stations.getFirst().getId();
        }

        model.addAttribute("stationSelectionForm", new StationSelectionForm(null));
        model.addAttribute("stations", stationService.getAll());
        return "correct_score_select_station";
    }

    /**
     * Select station, then show patrols left on this station
     */
    @PostMapping("/selectstation")
    public String selectStation(@ModelAttribute("stationSelectionForm") StationSelectionForm form,
                                BindingResult result,
                                Model model) {

        if (form.stationId() == null) {
            model.addAttribute("errormsg", "Du måste välja en kontroll");
            model.addAttribute("stations", stationService.getAll());
            return "correct_score_select_station";
        }

        return renderScoresForStation(form.stationId(), model);
    }


    /**
     * Used by redirect from startCorrectScore if the user has access to only one station
     * @param stationId
     * @param model
     * @return
     */
    @GetMapping("/selectstation")
    public String selectStationGet(@RequestParam Integer stationId, Model model) {
        return renderScoresForStation(stationId, model);
    }



    @GetMapping("/selectstation/{stationId}")
    public String selectStation(@PathVariable("stationId") Integer stationId, Model model) {

        return renderScoresForStation(stationId, model);
    }

    /**
     * Edit an existing score
     */
    @GetMapping("/score/{id}")
    public String editScore(@PathVariable Integer id, Model model) {
        Score score = scoreService.findByIdWithPatrolAndStation(id)
                .orElseThrow(() -> new IllegalArgumentException("Score not found"));

        model.addAttribute("score", score);
        model.addAttribute("stations", stationService.getAll());
        return "correct_score_edit";
    }


    /**
     * Update or delete a score
     */
    @PostMapping("/score")
    public String updateOrDelete(@ModelAttribute("score") Score score, @RequestParam String action, RedirectAttributes redirectAttributes) {

        Score scoreFromDb = scoreService.findByIdWithPatrolAndStation(score.getId()).orElseThrow(() -> new IllegalArgumentException("Score not found"));

        if ("delete".equals(action)) {
            System.out.println("score " + score);
            Patrol patrol = scoreFromDb.getPatrol();
            System.out.println("patrol " + patrol);

            patrol.getScores().remove(scoreFromDb); // triggers orphan removal
            patrolService.save(patrol);    // persist the change

            redirectAttributes.addFlashAttribute("confirmmsg", "Poängen borttagen");
            return "redirect:/correct/selectstation/" + scoreFromDb.getStation().getId();
        }

        scoreService.save(score);

        if (score.isVisitedWaypoint()) {
            redirectAttributes.addFlashAttribute("confirmmsg",
                    "Ändrat till " + scoreFromDb.getPatrol().getPatrolName() + " har passerat kontrollen.");
        } else {
            redirectAttributes.addFlashAttribute("confirmmsg",
                    "Ändrat till " + score.getScorePoint() + " poäng och " + score.getStylePoint() +
                            " stilpoäng för " + scoreFromDb.getPatrol().getPatrolName() +
                            " från " + scoreFromDb.getPatrol().getTroop() + ".");
        }


        return "redirect:/correct/selectstation/" + score.getStation().getId();
    }


    private String renderScoresForStation(Integer stationId, Model model) {
        Station station = stationService.getStationById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));
        model.addAttribute("scores", scoreService.getScoresForStation(station.getId()));
        model.addAttribute("station", station);
        return "correct_score_list";
    }

}
