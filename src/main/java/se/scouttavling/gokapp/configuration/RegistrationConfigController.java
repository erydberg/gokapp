package se.scouttavling.gokapp.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/registrationconfig")
public class RegistrationConfigController {

    final RegistrationConfigService service;

    @GetMapping
    public String getCurrentRegistrationConfig(Model model) {

        model.addAttribute("registrationconfig", service.getCurrentConfig());

        return "registration_config_edit";
    }


    @PostMapping
    public String save(@ModelAttribute("registrationconfig") RegistrationConfig registrationConfig, Model model, RedirectAttributes redirectAttributes) {
        service.save(registrationConfig);
        model.addAttribute("confirmmsg", "Konfigurationen för publik registrering är sparad");
        return "redirect:/admin";
    }
}
