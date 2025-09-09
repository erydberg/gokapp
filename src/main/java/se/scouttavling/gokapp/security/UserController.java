package se.scouttavling.gokapp.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // List all users
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "user_admin_list";
    }

    // Show form for creating a new user
    @GetMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user_admin_edit";
    }

    // Show form for editing an existing user
    @GetMapping("/{id}")
    public String editUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserById(id);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("errormsg", "Användaren hittades inte");
            return "redirect:/admin/user";
        }
        model.addAttribute("user", user);
        return "user_admin_edit";
    }

    // Save new or edited user
    @PostMapping
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errormsg", "Fyll i alla obligatoriska fält korrekt");
            return "user_admin_edit";
        }

        try {
            userService.save(user);
            redirectAttributes.addFlashAttribute("confirmmsg", "Användaren sparad");
        } catch (Exception e) {
            model.addAttribute("errormsg", "Kunde inte spara användaren: " + e.getMessage());
            return "admin_user_edit";
        }

        return "redirect:/admin/user";
    }

    // Delete a user
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("confirmmsg", "Användaren borttagen");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errormsg", "Kunde inte ta bort användaren: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }
}
