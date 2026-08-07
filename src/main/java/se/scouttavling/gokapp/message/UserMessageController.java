package se.scouttavling.gokapp.message;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class UserMessageController {

    private static final String DEFAULT_RETURN_URL = "/startmenu";

    private final UserMessageService userMessageService;
    private final UserService userService;

    /**
     * Acknowledges a message. Called with fetch from usermessages.js, which keeps the user on the
     * page with the state intact, but works as a plain form post as well.
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable("id") Integer id,
                                         @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                         @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                         Principal principal) {

        Optional<User> currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser.isPresent()) {
            try {
                userMessageService.markAsRead(id, currentUser.get());
            } catch (DataIntegrityViolationException e) {
                // the user managed to acknowledge the same message twice - already logged, all good
                System.out.println("Message " + id + " was already acknowledged by " + principal.getName());
            }
        }

        if ("XMLHttpRequest".equals(requestedWith)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(safeReturnUrl(returnUrl)))
                .build();
    }

    /**
     * Only local paths are accepted, so that the return address can not be used to send the user
     * off to another site.
     */
    private String safeReturnUrl(String returnUrl) {

        if (returnUrl == null || !returnUrl.startsWith("/") || returnUrl.startsWith("//")) {
            return DEFAULT_RETURN_URL;
        }

        try {
            URI.create(returnUrl);
        } catch (IllegalArgumentException e) {
            // not a usable address - send the user to the main menu instead of failing
            return DEFAULT_RETURN_URL;
        }

        return returnUrl;
    }
}