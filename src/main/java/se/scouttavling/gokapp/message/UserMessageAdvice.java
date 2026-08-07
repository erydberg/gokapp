package se.scouttavling.gokapp.message;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import se.scouttavling.gokapp.security.UserService;

import java.security.Principal;
import java.util.List;

/**
 * Makes the logged in user's unread messages available to every view, so that the message bar
 * can be rendered on any page without each controller having to know about it.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class UserMessageAdvice {

    private final UserMessageService userMessageService;
    private final UserService userService;

    @ModelAttribute("unreadMessages")
    public List<UserMessage> unreadMessages(Principal principal) {

        if (principal == null) {
            return List.of();
        }

        return userService.findUserByUsername(principal.getName())
                .map(userMessageService::getUnreadFor)
                .orElse(List.of());
    }

    /**
     * The page the user is currently on. Used as return address when a message is acknowledged
     * without javascript. Thymeleaf 3.1 no longer exposes #request, so it has to come from here.
     */
    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {

        String queryString = request.getQueryString();

        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
    }
}