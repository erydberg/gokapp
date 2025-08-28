package se.scouttavling.gokapp.start;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;

@RequestMapping("/")
@Controller
public class StartController {

    private final ConfigService configService;

    public StartController(ConfigService configService) {
        this.configService = configService;
    }

    @ModelAttribute("config")
    public Config loadConfig() {
        return configService.getCurrentConfig();
    }

    @GetMapping
    public String Start(Model model) {

        return "welcomepage";
    }

    @GetMapping("/startmenu")
    public String startMenu() {

        return "start_menu";
    }


    @GetMapping("/admin")
    public String adminStart() {

        return "admin_start";
    }


    @GetMapping("/login")
    public String login() {
        return "login"; // maps to login.html in src/main/resources/templates
    }

    @GetMapping("/loginfailed")
    public String loginerror(Model model) {
        model.addAttribute("error", "Felaktigt användarnamn eller lösenord");
        return "login";
    }

}
