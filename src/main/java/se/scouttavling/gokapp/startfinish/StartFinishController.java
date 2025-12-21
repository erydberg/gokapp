package se.scouttavling.gokapp.startfinish;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.patrol.Status;
import se.scouttavling.gokapp.patrol.StatusCounter;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.score.ScoreService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/startfinish")
@RequiredArgsConstructor
public class StartFinishController {


    private final PatrolService patrolService;
    private final ScoreService scoreService;

    @GetMapping
    public String loadStartAndFinish(@RequestParam(required = false) String sort,
                                     @RequestParam(required = false, defaultValue = "asc") String dir,
                                     Model model) {

        List<Patrol> patrols = patrolService.findAllSorted(sort, dir);
        Map<String, Long> statusCounter = StatusCounter.calculateAllStatuses(patrols);
        model.addAttribute("statusCounter", statusCounter);
        model.addAttribute("patrols", patrols);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        return "start_finish";
    }

    // ---- REST endpoints for status updates ----
    @PutMapping("/activate/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Integer id) {
        patrolService.updateStatus(id, Status.ACTIVE);
    }

    @PutMapping("/finished/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void finished(@PathVariable Integer id) {
        patrolService.updateStatus(id, Status.FINISHED);
    }

    @PutMapping("/resigned/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void resigned(@PathVariable Integer id) {
        patrolService.updateStatus(id, Status.RESIGNED);
    }

    @PutMapping("/registered/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void registered(@PathVariable Integer id) {
        patrolService.updateStatus(id, Status.REGISTERED);
    }



    @GetMapping("/patrol/{id}")
    public String viewPatrol(@PathVariable Integer id, Model model) {
        Patrol patrol = patrolService.getPatrolById(id).orElseThrow(() -> new IllegalArgumentException("Station not found"));
        List<Score> scores = scoreService.findByPatrol(id);

        model.addAttribute("patrol", patrol);
        model.addAttribute("scores", scores);

        return "startfinish_patrol";
    }


    /**
     * Edit an existing score from start_finish
     */
    @GetMapping("/score/{id}")
    public String editScore(@PathVariable Integer id, Model model) {
        Score score = scoreService.findByIdWithPatrolAndStation(id)
                .orElseThrow(() -> new IllegalArgumentException("Score not found"));

        model.addAttribute("score", score);
        //model.addAttribute("stations", stationService.getAll());
        return "startfinish_correct_score";
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
            return "redirect:/startfinish/patrol/" + scoreFromDb.getPatrol().getPatrolId();
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


        return "redirect:/startfinish/patrol/" + score.getPatrol().getPatrolId();
    }


}
