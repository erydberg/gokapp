package se.scouttavling.gokapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import se.scouttavling.gokapp.station.Station;

public class SecurityCheck {

    /*
    public static boolean isEditAllowedForCurrentUserOnStation(Station station) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = user.getUsername();

        return isUserAllowedToEdit(station.getStationUser(), user, currentUsername);
    }

    private static boolean isUserAllowedToEdit(String stationUser, User user, String currentUsername) {
        return isUserAdmin(user) ||
                (stationUser != null && stationUser.equalsIgnoreCase(currentUsername));
    }

    public static boolean isUserAdmin(org.springframework.security.core.userdetails.User user){
        for(GrantedAuthority authority : user.getAuthorities()){
            if(authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")){
                return true;
            }
        }
        return false;
    }

     */

}
