package se.scouttavling.gokapp.start;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.security.Role;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;

@Controller
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final UserService userService;
    private final ConfigService configService;

    @GetMapping
    public String setupPage(Model model) {
        model.addAttribute("setupForm", new SetupForm());
        return "setup";
    }

    @PostMapping
    public String processSetup(@ModelAttribute("setupForm") SetupForm form, Model model) {
        String error = validate(form);
        if (error != null) {
            model.addAttribute("errormsg", error);
            return "setup";
        }

        User admin = User.builder()
                .username(form.getUsername().trim())
                .password(form.getPassword())
                .build();
        admin.addRole(Role.ROLE_ADMIN);
        userService.save(admin);

        Config config = new Config();
        config.setName(form.getSystemName().trim());
        config.setPhone(form.getPhone());
        config.setAllowPublicResult(form.isAllowPublicResult());
        config.setUseQr(form.isUseQr());
        configService.save(config);

        return "redirect:/login?setup";
    }

    private String validate(SetupForm form) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            return "Användarnamn är obligatoriskt";
        }
        if (form.getPassword() == null || form.getPassword().length() < 8) {
            return "Lösenordet måste vara minst 8 tecken";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            return "Lösenorden matchar inte";
        }
        if (form.getSystemName() == null || form.getSystemName().isBlank()) {
            return "Tävlingens namn är obligatoriskt";
        }
        return null;
    }
}
