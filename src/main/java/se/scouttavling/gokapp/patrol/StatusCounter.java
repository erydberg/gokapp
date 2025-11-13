package se.scouttavling.gokapp.patrol;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusCounter {

    public StatusCounter() throws IllegalAccessException {
        throw new IllegalAccessException("Only static methods");
    }


    public static Map<String, Long> calculateAllStatuses(List<Patrol> patrols) {
        return patrols.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));
    }

}
