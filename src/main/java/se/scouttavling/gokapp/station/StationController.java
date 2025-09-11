package se.scouttavling.gokapp.station;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/station")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    private final UserService userService;


    @GetMapping
    public String getAll(Model model) {

        List<Station> stations = stationService.getAll();
        model.addAttribute("stations", stations);

        return "station_list";
    }

    @GetMapping("/new")
    public String displayForm(Model model) {
        model.addAttribute("station", new Station());
        model.addAttribute("users", userService.findAllUsers());

        return "station_edit";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        Station station = stationService.getStationById(id).
                orElseThrow(() -> new IllegalArgumentException("Invalid station Id:" + id));
        model.addAttribute("station", station);
        model.addAttribute("users", userService.findAllUsers());

        return "station_edit";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("station") Station station, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        System.out.println("Saving a station");
        if(bindingResult.hasErrors()) {
            System.out.println("Errors exists");
            model.addAttribute("errormsg", "Fyll i alla obligatoriska uppgifter");
            model.addAttribute("station", station);
            model.addAttribute("users", userService.findAllUsers());
            return "station_edit";
        }

        System.out.println("Fetching user from db based on id " + station.getStationUser().getId());
        // Fetch the User from DB based on ID set in the form
        if (station.getStationUser() != null && station.getStationUser().getId() != null) {
            User user = userService.findUserById(station.getStationUser().getId()).orElse(null);
            station.setStationUser(user);
        } else {
            station.setStationUser(null); // in case no user is selected
        }

        stationService.save(station);
        redirectAttributes.addFlashAttribute("confirmmsg", "Kontrollen är sparad");

        return "redirect:/admin/station";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        stationService.delete(id);
        redirectAttributes.addFlashAttribute("confirmmsg", "Kontrollen borttagen");

        return "redirect:/admin/station";
    }

}
