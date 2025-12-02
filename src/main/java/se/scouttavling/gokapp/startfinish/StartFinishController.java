package se.scouttavling.gokapp.startfinish;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.patrol.Status;
import se.scouttavling.gokapp.patrol.StatusCounter;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/startfinish")
public class StartFinishController {

    @Autowired
    private PatrolService patrolService;

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

    @GetMapping("/viewpatrol/{id}")
    public String viewPatrol(@PathVariable Integer id, Model model) {
        Patrol patrol = patrolService.getPatrolById(id).orElseThrow(() -> new IllegalArgumentException("Station not found"));

        model.addAttribute("patrol", patrol);
        model.addAttribute("backurl", "/startfinish");
        return "viewpatrol";
    }


}
