package se.scouttavling.gokapp.start;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.configuration.RegistrationConfig;
import se.scouttavling.gokapp.configuration.RegistrationConfigService;

@RequestMapping("/")
@Controller
@RequiredArgsConstructor
public class StartController {

    private final ConfigService configService;
    private final RegistrationConfigService registrationConfigService;


    @ModelAttribute("config")
    public Config loadConfig() {
        return configService.getCurrentConfig();
    }

    @GetMapping
    public String Start(Model model) {
        RegistrationConfig registrationConfig = registrationConfigService.getCurrentConfig();
        try {
            if (Boolean.TRUE.equals(registrationConfig.getAllowPublicRegistration()) && RegistrationChecker.isOpenToday(registrationConfig.getFirstRegisterDay(), registrationConfig.getLastRegisterDay())){
                model.addAttribute("registrationOpen",true);
                model.addAttribute("registrationConfig", registrationConfig);
            }else{
                model.addAttribute("registrationOpen",false);
            }
        } catch (Exception e) {
            model.addAttribute("registrationOpen",false);
        }

        return "welcomepage";
    }

    @GetMapping("/controller/startmenu")
    public String startMenu() {

        return "start_menu";
    }


    @GetMapping("/admin")
    public String adminStart() {

        return "admin_start";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginfailed")
    public String loginerror(Model model) {
        model.addAttribute("error", "Felaktigt användarnamn eller lösenord");
        return "login";
    }

}
