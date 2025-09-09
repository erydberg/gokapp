package se.scouttavling.gokapp.start;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import se.scouttavling.gokapp.security.Role;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserService;


@Component
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureAdminUserExists() {
    //    if (userService.countUsers() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin"); // Make sure UserService encodes the password
            admin.addRole(Role.ROLE_ADMIN);
            userService.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin");
   //     }
    }
}

