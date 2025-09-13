package se.scouttavling.gokapp.patrol;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/patrols")
@RequiredArgsConstructor
public class ViewPatrolsController {

    private final PatrolService patrolService;

    @GetMapping
    public String listAllPatrols(Model model) {

        model.addAttribute("patrols", patrolService.getAllPatrolsSortByName());
        return "patrol_view_list";
    }
}
