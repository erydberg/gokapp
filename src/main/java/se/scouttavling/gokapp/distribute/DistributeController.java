package se.scouttavling.gokapp.distribute;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/distribute")
public class DistributeController {

    final ConfigService configService;
    final PatrolService patrolService;
    final StationService stationService;
    final TrackService trackService;


    @ModelAttribute("config")
    public Config loadConfig(Model model) {
        return configService.getCurrentConfig();
    }


    @GetMapping
    public String start(Model model) {

        return "distribute_start";
    }


    /**
     * Takes all patrols and distribute them evenly over all stations
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/all")
    public String allPatrolsOnStations(RedirectAttributes redirectAttributes) {
        List<Patrol> patrols = patrolService.getAllPatrols();
        List<Station> stations = stationService.getAll();

        Distribute distribute = new Distribute(patrols, stations);
        String distributeMessage = distribute.distributePatrols();

        patrolService.saveAll(patrols);

        redirectAttributes.addFlashAttribute("confirmmsg", distributeMessage);
        return "redirect:/admin/distribute";
    }


    /**
     * This method fetches all patrols on a specific track and distribute them over all stations and
     * does this for each track to help in situations where there are many patrols on some tracks but much less on other tracks
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/basedontrack")
    public String distributePatrolsBasedOnTrack(RedirectAttributes redirectAttributes) {
        List<Station> stations = stationService.getAll();
        List<Track> tracks = trackService.findAllTracks();

        List<String> messages = new ArrayList<>();

        for (Track track : tracks) {
            List<Patrol> patrols = patrolService.getAllPatrolsByTrack(track);
            Distribute distribute = new Distribute(patrols, stations, track.getName());
            messages.add(distribute.distributePatrols());
            patrolService.saveAll(patrols);
        }

        redirectAttributes.addFlashAttribute("confirmmsg", messages);
        return "redirect:/admin/distribute";
    }

}
