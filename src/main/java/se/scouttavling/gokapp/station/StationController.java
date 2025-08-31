package se.scouttavling.gokapp.station;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/station")
@RequiredArgsConstructor
public class StationController {

    final private StationService stationService;


    @GetMapping
    public String getAll(Model model) {

        List<Station> stations = stationService.getAll();
        model.addAttribute("stations", stations);

        return "station_list";
    }

    @GetMapping("/new")
    public String displayForm(Model model) {
        model.addAttribute("station", new Station());

        return "station_edit";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        Station station = stationService.getStationById(id).
                orElseThrow(() -> new IllegalArgumentException("Invalid station Id:" + id));
        model.addAttribute("station", station);

        return "station_edit";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("station") Station station, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("errormsg", "Fyll i alla obligatoriska uppgifter");
            model.addAttribute("station", station);
            return "station_edit";
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
