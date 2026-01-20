package se.scouttavling.gokapp.patrol;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

import java.util.List;

@RequestMapping("/admin/patrol")
@RequiredArgsConstructor
@Controller
public class PatrolAdminController {
    private final PatrolService patrolService;
    private final TrackService trackService;
    private final StationService stationService;

    @ModelAttribute("tracks")
    public List<Track> populateTracks() {
        return trackService.findAllTracks();
    }

    @ModelAttribute("stations")
    public List<Station> populateStations() {
        return stationService.getAll();
    }

    @GetMapping
    public String listAllPatrols(@RequestParam(required = false) String sort,
                                 @RequestParam(required = false, defaultValue = "asc") String dir,
                                 Model model) {
        List<Patrol> patrols = patrolService.findAllSorted(sort, dir);
        model.addAttribute("patrols", patrols);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "patrol_admin_list";
    }


    @GetMapping("/new")
    public String newPatrolForm(Model model) {
        model.addAttribute("patrol", new Patrol());
        model.addAttribute("statuslist", Status.values());
        return "patrol_admin_edit";  // Thymeleaf form
    }


    /**
     * Go to edit mode of a patrol loaded from backend database
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        Patrol patrol = patrolService.getPatrolById(id).
                orElseThrow(() -> new IllegalArgumentException("Invalid patrol Id:" + id));
        model.addAttribute("patrol", patrol);
        model.addAttribute("statuslist", Status.values());

        return "patrol_admin_edit";
    }


    @PostMapping
    public String save(@Valid @ModelAttribute("patrol") Patrol updatedPatrol, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errormsg", "Fyll i alla obligatoriska uppgifter");
            model.addAttribute("patrol", updatedPatrol);
            model.addAttribute("statuslist", Status.values());
            return "patrol_admin_edit";
        }

        if (updatedPatrol.getPatrolId() != null) {
            System.out.println("En uppdatring av patrull med id " + updatedPatrol.getPatrolId());
            Patrol patrolFromDb = patrolService.getPatrolById(updatedPatrol.getPatrolId()).orElseThrow(() -> new IllegalArgumentException("Patrol not found"));
            patrolFromDb.setPatrolName(updatedPatrol.getPatrolName());
            patrolFromDb.setTroop(updatedPatrol.getTroop());
            patrolFromDb.setStatus(updatedPatrol.getStatus());
            patrolFromDb.setTrack(updatedPatrol.getTrack());
            patrolFromDb.setLeaderContact(updatedPatrol.getLeaderContact());
            patrolFromDb.setLeaderContactPhone(updatedPatrol.getLeaderContactPhone());
            patrolFromDb.setLeaderContactMail(updatedPatrol.getLeaderContactMail());
            patrolFromDb.setNote(updatedPatrol.getNote());
            patrolFromDb.setPaid(updatedPatrol.getPaid());
            patrolFromDb.setDateRegistered(updatedPatrol.getDateRegistered());
            patrolFromDb.setEndTime(updatedPatrol.getEndTime());
            patrolFromDb.setStartTime(updatedPatrol.getStartTime());
            patrolFromDb.setStartStation(updatedPatrol.getStartStation());
            patrolFromDb.setExternalId(updatedPatrol.getExternalId());
            patrolFromDb.setMembers(updatedPatrol.getMembers());

            patrolService.save(patrolFromDb);
        } else {
            System.out.println("En ny patrull sparas från admin-gränssnittet");
            patrolService.save(updatedPatrol);
        }

        redirectAttributes.addFlashAttribute("confirmmsg", "Patrullen är sparad");

        return "redirect:/admin/patrol";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        patrolService.deletePatrolById(id);
        redirectAttributes.addFlashAttribute("confirmmsg", "Kontrollen borttagen");

        return "redirect:/admin/patrol";
    }


    /**
     * Switch between paid / not paid
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/setpaid/{id}")
    @ResponseBody
    public ResponseEntity<String> setPaid(
            @PathVariable("id") Integer id,
            @RequestParam("status") boolean status) {

        return patrolService.getPatrolById(id)
                .map(patrol -> {
                    patrol.setPaid(status);
                    patrolService.save(patrol);
                    return ResponseEntity.ok("updated");
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
