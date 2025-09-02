package se.scouttavling.gokapp.start;

import se.scouttavling.gokapp.configuration.RegistrationConfig;

import java.time.LocalDate;

public class RegistrationChecker {


    private RegistrationChecker() {
    }

    public static boolean isOpenForRegistration(RegistrationConfig config, int noOfRegisteredPatrolsNow) {
        int maxPatrols = (config.getMaxPatrols() != null) ? config.getMaxPatrols() : 0;
        boolean allowRegistration = (config.getAllowPublicRegistration() != null) && config.getAllowPublicRegistration();

        return allowRegistration
                && isOpenToday(config.getFirstRegisterDay(), config.getLastRegisterDay())
                && isItSeatsLeft(maxPatrols, noOfRegisteredPatrolsNow);
    }

    public static boolean isOpenToday(LocalDate firstRegistrationDate, LocalDate lastRegistrationDate) {
        if (firstRegistrationDate == null || lastRegistrationDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        // Check if today is between first and last (inclusive)
        return (!today.isBefore(firstRegistrationDate)) && (!today.isAfter(lastRegistrationDate));
    }

    private static boolean isItSeatsLeft(int maxSeats, int registeredPatrolsNow) {
        if (maxSeats == 0) {
            return true; // Unlimited seats
        }
        return maxSeats > registeredPatrolsNow;
    }
}
