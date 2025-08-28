package se.scouttavling.gokapp.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin/config")
@Controller
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public String viewConfig(Model model) {
        Config config = configService.getCurrentConfig();
        model.addAttribute("config", config);

        return "config";
    }

    @PostMapping
    public String save(Config config, Model model) {
        configService.save(config);
        model.addAttribute("confirmmsg", "Konfigurationen är sparad");

        return "admin_start";
    }
}
