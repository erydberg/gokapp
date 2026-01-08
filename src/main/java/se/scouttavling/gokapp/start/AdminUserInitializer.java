package se.scouttavling.gokapp.start;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
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
    @Order(1)
    public void ensureAdminUserExists() {
        System.out.println("Number of users in the system " + userService.countUsers());
        if (userService.countUsers() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.addRole(Role.ROLE_ADMIN);
            userService.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin");
        } else {
            System.out.println("One or more users already in place, no default admin user created");
        }
        System.out.println("noOfUsers " + userService.countUsers());
        userService.findAllUsers().forEach(user -> System.out.println(user.getUsername()));
    }
}

