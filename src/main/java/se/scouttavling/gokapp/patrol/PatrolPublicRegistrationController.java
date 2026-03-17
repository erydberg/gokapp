package se.scouttavling.gokapp.patrol;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.configuration.RegistrationConfig;
import se.scouttavling.gokapp.configuration.RegistrationConfigService;
import se.scouttavling.gokapp.start.RegistrationChecker;
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
        RegistrationConfig registrationConfig = registrationConfigService.getCurrentConfig();
        int noOfPatrols = patrolService.getAllPatrols().size();
        if (RegistrationChecker.isOpenForRegistration(registrationConfig, noOfPatrols)) {
            model.addAttribute("config", configService.getCurrentConfig());
            model.addAttribute("patrolPublicDto", new PatrolPublicDto());
            model.addAttribute("tracks", trackService.findAllTracks());
            model.addAttribute("registrationconfig", registrationConfig);
            model.addAttribute("registeredpatrols", noOfPatrols);
            return "patrol_public_registration";
        } else {
            model.addAttribute("config", configService.getCurrentConfig());
            model.addAttribute("registrationconfig", registrationConfig);
            return "patrol_public_registration_closed";
        }

    }

    @PostMapping
    public String save(@Valid @ModelAttribute("patrolPublicDto") PatrolPublicDto patrolPublicDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        model.addAttribute("config", configService.getCurrentConfig());
        model.addAttribute("registrationconfig", registrationConfigService.getCurrentConfig());

        if (bindingResult.hasErrors()) {
            model.addAttribute("tracks", trackService.findAllTracks());
            return "patrol_public_registration";
        }

        Track track = trackService.findTrackById(patrolPublicDto.getTrack())
                .orElseThrow(() -> new IllegalArgumentException("Invalid track id"));

        Patrol patrol = PatrolMapper.fromPublicDto(patrolPublicDto, track);
        patrol.setStatus(Status.REGISTERED);
        Patrol savedPatrol = patrolService.save(patrol);

        model.addAttribute("patrol", patrol);

        redirectAttributes.addFlashAttribute("confirmmsg", "Patrullen är registrerad");
        redirectAttributes.addAttribute("patrolId", savedPatrol.getPatrolId());
        return "redirect:/public/register/success";
    }

    @GetMapping("/success")
    public String success(@RequestParam("patrolId") Integer patrolId, Model model) {

        Patrol patrol = patrolService.getPatrolById(patrolId)
                .orElseThrow(() -> new EntityNotFoundException("Patrol not found"));
        model.addAttribute("patrol", patrol);

        model.addAttribute("config", configService.getCurrentConfig());
        model.addAttribute("registrationconfig", registrationConfigService.getCurrentConfig());

        return "patrol_public_registration_success";
    }

}
