package se.scouttavling.gokapp.patrol;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.configuration.RegistrationConfig;
import se.scouttavling.gokapp.configuration.RegistrationConfigService;
import se.scouttavling.gokapp.track.Track;
import se.scouttavling.gokapp.track.TrackService;

@Controller
@RequestMapping("/public/register")
@RequiredArgsConstructor
public class PatrolPublicRegistrationController {

    private final ConfigService configService;
    private final RegistrationConfigService registrationConfigService;
    private final TrackService trackService;
    private final PatrolService patrolService;




    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("config", configService.getCurrentConfig());
        model.addAttribute("patrolPublicDto", new PatrolPublicDto());
        model.addAttribute("tracks", trackService.findAllTracks());
        model.addAttribute("registrationconfig", registrationConfigService.getCurrentConfig());
        return "patrol_public_registration"; // thymeleaf template
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("patrolPublicDto") PatrolPublicDto patrolPublicDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("config", configService.getCurrentConfig());
            model.addAttribute("tracks", trackService.findAllTracks());
            model.addAttribute("registrationconfig", registrationConfigService.getCurrentConfig());
            return "patrol_public_registration";
        }

        Track track = trackService.findTrackById(patrolPublicDto.getTrack())
                .orElseThrow(() -> new IllegalArgumentException("Invalid track id"));

        Patrol patrol = PatrolMapper.fromPublicDto(patrolPublicDto, track);
        patrolService.save(patrol);

        model.addAttribute("confirmmsg", "Patrullen är registrerad!");
        return "register_success";
    }

}
