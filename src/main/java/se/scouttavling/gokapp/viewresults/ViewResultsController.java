package se.scouttavling.gokapp.viewresults;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

import java.util.List;

@Controller
@RequestMapping("/results")
@RequiredArgsConstructor
public class ViewResultsController {

    private final PatrolService patrolService;
    private final TrackService trackService;

    @GetMapping("/bytrack")
    public String selectTrack(Model model) {

        model.addAttribute("tracks", trackService.findAllTracks());
        return "results_select_track";
    }

    @GetMapping("/bytrack/{id}")
    public String viewResultsByTrack(@PathVariable("id") Integer id, Model model) {

        Track selectedTrack = trackService.findTrackById(id).orElseThrow(() -> new IllegalArgumentException("Invalid track Id:" + id));
        model.addAttribute("selectedTrack", selectedTrack);

        List<Patrol> patrols = patrolService.getAllPatrolsByTrackSortedByScore(selectedTrack);
                model.addAttribute("patrols", patrols);

        return "results_by_track";
    }
}
