package se.scouttavling.gokapp.start;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.scouttavling.gokapp.configuration.RegistrationConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationCheckerTest {

    private RegistrationConfig config;

    @BeforeEach
    void init() {
        config = new RegistrationConfig();
    }

    @Test
    void shouldReturnTrueIfAllParametersAreGood() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(tomorrow());
        config.setMaxPatrols(200);

        assertTrue(RegistrationChecker.isOpenForRegistration(config, 100));
    }

    @Test
    void shouldReturnFalseSinceRegistrationIsOff() {
        config.setAllowPublicRegistration(false);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(tomorrow());

        assertFalse(RegistrationChecker.isOpenForRegistration(config, 100));
    }

    @Test
    void shouldReturnFalseSinceDateIsHistory() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(yesterday());

        assertFalse(RegistrationChecker.isOpenForRegistration(config, 100));
    }

    @Test
    void shouldReturnTrueSinceEndDateIsTomorrow() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(tomorrow());
        config.setMaxPatrols(200);

        assertTrue(RegistrationChecker.isOpenForRegistration(config, 100));
    }

    @Test
    void shouldReturnFalseSinceStartDateIsTomorrow() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(tomorrow());
        config.setLastRegisterDay(tomorrow());
        config.setMaxPatrols(200);

        assertFalse(RegistrationChecker.isOpenForRegistration(config, 100));
    }

    @Test
    void shouldReturnFalseSinceFull() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(tomorrow());
        config.setMaxPatrols(200);

        assertFalse(RegistrationChecker.isOpenForRegistration(config, 200));
    }

    @Test
    void shouldReturnTrueSinceNotFull() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(yesterday());
        config.setLastRegisterDay(tomorrow());
        config.setMaxPatrols(300);

        assertTrue(RegistrationChecker.isOpenForRegistration(config, 200));
    }

    @Test
    void shouldReturnTrueSinceStartsToday() {
        config.setAllowPublicRegistration(true);
        config.setFirstRegisterDay(LocalDate.now());
        config.setLastRegisterDay(tomorrow());

        assertTrue(RegistrationChecker.isOpenForRegistration(config, 200));
    }

    @Test
    void shouldReturnFalseSinceNoAllowIsSet() {
        assertFalse(RegistrationChecker.isOpenForRegistration(config, 200));
    }

    private LocalDate yesterday() {
        return LocalDate.now().minusDays(1);
    }

    private LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }
}
