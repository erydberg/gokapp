package se.scouttavling.gokapp.patrol;

import se.scouttavling.gokapp.track.Track;

public class PatrolMapper {
    public static Patrol fromPublicDto(PatrolPublicDto dto, Track track) {
        Patrol patrol = new Patrol();
        patrol.setPatrolName(dto.getPatrolName());
        patrol.setTroop(dto.getTroop());
        patrol.setTrack(track); // resolve by ID in service
        patrol.setLeaderContact(dto.getLeaderContact());
        patrol.setLeaderContactMail(dto.getLeaderContactMail());
        patrol.setLeaderContactPhone(dto.getLeaderContactPhone());
        return patrol;
    }
}
