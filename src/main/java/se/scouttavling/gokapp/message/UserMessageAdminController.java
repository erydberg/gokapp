package se.scouttavling.gokapp.message;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/message")
@RequiredArgsConstructor
public class UserMessageAdminController {

    private final UserMessageService userMessageService;
    private final UserService userService;

    @GetMapping
    public String getAll(Model model) {

        List<UserMessage> messages = userMessageService.getAll();

        Map<Integer, Long> readCounts = new HashMap<>();
        for (UserMessage message : messages) {
            readCounts.put(message.getId(), userMessageService.countReadersFor(message.getId()));
        }

        model.addAttribute("messages", messages);
        model.addAttribute("readCounts", readCounts);
        model.addAttribute("userCount", userService.countUsers());

        return "message_list";
    }

    @GetMapping("/new")
    public String displayForm(Model model) {

        model.addAttribute("message", new UserMessage());

        return "message_edit";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {

        UserMessage message = userMessageService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
        model.addAttribute("message", message);

        return "message_edit";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("message") UserMessage message, BindingResult bindingResult,
                       Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errormsg", "Fyll i alla obligatoriska uppgifter");
            model.addAttribute("message", message);
            return "message_edit";
        }

        userMessageService.save(message, principal.getName());
        redirectAttributes.addFlashAttribute("confirmmsg", "Meddelandet är sparat");

        return "redirect:/admin/message";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

        userMessageService.delete(id);
        redirectAttributes.addFlashAttribute("confirmmsg", "Meddelandet är borttaget");

        return "redirect:/admin/message";
    }

    @GetMapping("/{id}/readers")
    public String readers(@PathVariable("id") Integer id, Model model) {

        UserMessage message = userMessageService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));

        List<MessageRead> readers = userMessageService.getReadersFor(id);
        Set<Long> readerIds = readers.stream()
                .map(messageRead -> messageRead.getUser().getId())
                .collect(Collectors.toSet());

        List<User> notReadYet = userService.findAllUsers().stream()
                .filter(user -> !readerIds.contains(user.getId()))
                .toList();

        model.addAttribute("message", message);
        model.addAttribute("readers", readers);
        model.addAttribute("notReadYet", notReadYet);

        return "message_readers";
    }
}