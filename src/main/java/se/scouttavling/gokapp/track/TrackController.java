package se.scouttavling.gokapp.track;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/track")
@RequiredArgsConstructor
public class TrackController {

    final private TrackService trackService;

    @GetMapping
    public String listTracks(Model model) {
        System.out.println("List tracks");
        List<Track> tracks = trackService.findAllTracks();
        model.addAttribute("tracks", tracks);

        return "track_list"; // Thymeleaf template: tracks.html
    }

    @GetMapping("/new")
    public String displayForm(Model model) {
        model.addAttribute("track", new Track());
        return "track_edit";
    }

    @PostMapping
    public String saveTrack(@ModelAttribute Track track, RedirectAttributes redirectAttributes) {
        System.out.println("Save track");
        trackService.saveTrack(track);
        redirectAttributes.addFlashAttribute("confirmmsg", "Klassen är sparad");
        return "redirect:/admin/track";
    }


    @GetMapping("/{id}")
    public String editTrack(@PathVariable("id") Integer id, Model model) {
        Track track = trackService.findTrackById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid track Id:" + id));
        model.addAttribute("track", track);
        return "track_edit";
    }


    @PostMapping("/{id}/delete")
    public String deleteTrack(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        trackService.deleteTrack(id);

        redirectAttributes.addFlashAttribute("confirmmsg","Klassen är borttagen");
        return "redirect:/admin/track";
    }
}
