package se.scouttavling.gokapp.score;

/**
 * Thrown when a score/style point submitted for a station isn't one that station can actually
 * award (outside min-max range, not one of a distinct-score station's values, or a non-zero
 * score for a waypoint station). Guards against a tampered request bypassing the UI's radio
 * buttons - see ScoreService.save.
 */
public class InvalidScoreException extends RuntimeException {

    public InvalidScoreException(String message) {
        super(message);
    }
}
