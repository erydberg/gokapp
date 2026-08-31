package se.scouttavling.gokapp.patrol;

import org.junit.jupiter.api.Test;
import se.scouttavling.gokapp.score.Score;
import se.scouttavling.gokapp.station.Station;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolRankingTest {

    @Test
    void ranking() {


    }

    @Test
    void distinctScoreStationRanksBySortedPosition() {
        Station station = Station.builder()
                .stationName("Distinkt")
                .useDistinctScores(true)
                .distinctScores("1,4,8,16")
                .maxScore(16)
                .build();

        assertEquals(0, station.rankOfScore(16));
        assertEquals(1, station.rankOfScore(8));
        assertEquals(2, station.rankOfScore(4));
        assertEquals(3, station.rankOfScore(1));
        assertEquals(-1, station.rankOfScore(7));
    }

    @Test
    void rangeStationRankIsArithmeticDistanceFromMax() {
        Station station = Station.builder()
                .stationName("Intervall")
                .minScore(0)
                .maxScore(10)
                .build();

        assertEquals(0, station.rankOfScore(10));
        assertEquals(1, station.rankOfScore(9));
        assertEquals(10, station.rankOfScore(0));
    }

    @Test
    void secondBestOnDistinctStationIsCreditedAtRankOneNotArithmeticOffset() {
        Station station = Station.builder()
                .stationName("Distinkt")
                .useDistinctScores(true)
                .distinctScores("1,4,8,16")
                .maxScore(16)
                .build();

        Patrol patrol = Patrol.builder().build();
        patrol.getScores().add(Score.builder()
                .station(station)
                .scorePoint(8) // second-highest of the four allowed values
                .build());

        assertEquals(1, patrol.getNumberOfXPoints(1));
        assertEquals(0, patrol.getNumberOfXPoints(8));
    }

    @Test
    void tiedTotalsAreBrokenByStationRankAcrossMixedStationTypes() {
        Station rangeStation = Station.builder()
                .stationName("Intervall")
                .minScore(0)
                .maxScore(10)
                .build();
        // Distinct list where the top two values are 1 apart, same as the range station's
        // step size, so swapping which station each patrol "wins" keeps the totals tied.
        Station distinctStation = Station.builder()
                .stationName("Distinkt")
                .useDistinctScores(true)
                .distinctScores("0,9,10")
                .maxScore(10)
                .build();

        Patrol topOnRangeSecondOnDistinct = Patrol.builder().build();
        topOnRangeSecondOnDistinct.getScores().add(Score.builder().station(rangeStation).scorePoint(10).build());
        topOnRangeSecondOnDistinct.getScores().add(Score.builder().station(distinctStation).scorePoint(9).build());

        Patrol secondOnRangeTopOnDistinct = Patrol.builder().build();
        secondOnRangeTopOnDistinct.getScores().add(Score.builder().station(rangeStation).scorePoint(9).build());
        secondOnRangeTopOnDistinct.getScores().add(Score.builder().station(distinctStation).scorePoint(10).build());

        assertEquals(19, topOnRangeSecondOnDistinct.getTotalScore());
        assertEquals(19, secondOnRangeTopOnDistinct.getTotalScore());

        // Both patrols have exactly one outright win (rank 0) and one second-best (rank 1),
        // so the two entrants are genuinely tied by this measure regardless of which
        // station type each win came from.
        assertEquals(1, topOnRangeSecondOnDistinct.getNumberOfMaxPoints());
        assertEquals(1, secondOnRangeTopOnDistinct.getNumberOfMaxPoints());
        assertEquals(1, topOnRangeSecondOnDistinct.getNumberOfXPoints(1));
        assertEquals(1, secondOnRangeTopOnDistinct.getNumberOfXPoints(1));
        assertEquals(0, topOnRangeSecondOnDistinct.compareTo(secondOnRangeTopOnDistinct));
    }

    @Test
    void tiedTotalsFromRealCompetitionDataAreBrokenByDistinctStationRank() {
        // K1-K4: range 0-10. K8: distinct 0,5,10,15,20. (K5-K7,K9 untouched by either patrol.)
        Station k1 = Station.builder().stationName("K1").minScore(0).maxScore(10).build();
        Station k2 = Station.builder().stationName("K2").minScore(0).maxScore(10).build();
        Station k3 = Station.builder().stationName("K3").minScore(0).maxScore(10).build();
        Station k4 = Station.builder().stationName("K4").minScore(0).maxScore(10).build();
        Station k8 = Station.builder().stationName("K8").useDistinctScores(true).distinctScores("0,5,10,15,20").maxScore(20).build();

        Patrol aventyrare2 = Patrol.builder().build();
        aventyrare2.getScores().add(Score.builder().station(k1).scorePoint(10).stylePoint(1).build());
        aventyrare2.getScores().add(Score.builder().station(k2).scorePoint(6).stylePoint(1).build());
        aventyrare2.getScores().add(Score.builder().station(k3).scorePoint(10).stylePoint(1).build());
        aventyrare2.getScores().add(Score.builder().station(k4).scorePoint(5).stylePoint(0).build());
        aventyrare2.getScores().add(Score.builder().station(k8).scorePoint(15).stylePoint(0).build());

        Patrol aventyrare1 = Patrol.builder().build();
        aventyrare1.getScores().add(Score.builder().station(k1).scorePoint(8).stylePoint(1).build());
        aventyrare1.getScores().add(Score.builder().station(k2).scorePoint(7).stylePoint(0).build());
        aventyrare1.getScores().add(Score.builder().station(k3).scorePoint(10).stylePoint(1).build());
        aventyrare1.getScores().add(Score.builder().station(k4).scorePoint(1).stylePoint(0).build());
        aventyrare1.getScores().add(Score.builder().station(k8).scorePoint(20).stylePoint(1).build());

        // Both total 49 (46 score + 3 style), so the tie goes to the rank cascade.
        assertEquals(49, aventyrare2.getTotalScore());
        assertEquals(49, aventyrare1.getTotalScore());
        assertEquals(46, aventyrare2.getTotalScorePoint());
        assertEquals(46, aventyrare1.getTotalScorePoint());

        // Both won exactly 2 stations outright (Äventyrare2: K1,K3 / Äventyrare1: K3,K8) - still tied.
        assertEquals(2, aventyrare2.getNumberOfMaxPoints());
        assertEquals(2, aventyrare1.getNumberOfMaxPoints());

        // Tiebreak resolves at rank 1 ("second-best"): Äventyrare2's K8=15 is the true
        // second-highest of the distinct set {0,5,10,15,20}, crediting them a rank-1 win
        // that Äventyrare1 has none of (their best non-max result, K1=8, is rank 2 on a
        // range station). Äventyrare2 must therefore sort ahead of Äventyrare1.
        assertEquals(1, aventyrare2.getNumberOfXPoints(1));
        assertEquals(0, aventyrare1.getNumberOfXPoints(1));
        assertTrue(aventyrare2.compareTo(aventyrare1) < 0);
    }

    /**
     * Confirmed product decision: the tiebreak ranks by ORDINAL POSITION among a station's
     * achievable values ("2nd-best", "3rd-best", ...), never by the point gap that position
     * represents. A distinct station's gaps between values (e.g. 0,5,10,14) must not make its
     * "2nd-best" worth more or less than a range station's "2nd-best" (always a 1-point gap).
     * If this test starts failing because someone made the cascade point-gap-aware, that's an
     * intentional rule change that needs a product decision, not a bug fix.
     */
    @Test
    void tiebreakIgnoresPointGapSizeAndOnlyCountsOrdinalPosition() {
        Station s1 = Station.builder().stationName("S1").minScore(0).maxScore(10).build();
        Station s2 = Station.builder().stationName("S2").useDistinctScores(true).distinctScores("0,5,10,14").maxScore(14).build();
        Station s3 = Station.builder().stationName("S3").minScore(0).maxScore(10).build();

        Patrol patrol1 = Patrol.builder().build();
        patrol1.getScores().add(Score.builder().station(s1).scorePoint(10).stylePoint(1).build()); // max, rank 0
        patrol1.getScores().add(Score.builder().station(s2).scorePoint(10).stylePoint(1).build()); // 2nd of 4, rank 1 (4-point gap to max)
        patrol1.getScores().add(Score.builder().station(s3).scorePoint(9).stylePoint(1).build());  // rank 1 (1-point gap to max)

        Patrol patrol2 = Patrol.builder().build();
        patrol2.getScores().add(Score.builder().station(s1).scorePoint(9).stylePoint(1).build());  // rank 1 (1-point gap to max)
        patrol2.getScores().add(Score.builder().station(s2).scorePoint(14).stylePoint(1).build()); // max, rank 0
        patrol2.getScores().add(Score.builder().station(s3).scorePoint(6).stylePoint(1).build());  // rank 4

        assertEquals(29, patrol1.getTotalScorePoint());
        assertEquals(29, patrol2.getTotalScorePoint());

        // One outright win each - still tied.
        assertEquals(1, patrol1.getNumberOfMaxPoints());
        assertEquals(1, patrol2.getNumberOfMaxPoints());

        // Patrol 1's two rank-1 results outnumber patrol 2's one, even though one of patrol 1's
        // rank-1s (S2) represents a bigger real point gap (4) than patrol 2's single rank-1 (1).
        // That gap must not matter - only the count of rank-1 stations does.
        assertEquals(2, patrol1.getNumberOfXPoints(1));
        assertEquals(1, patrol2.getNumberOfXPoints(1));
        assertTrue(patrol1.compareTo(patrol2) < 0);
    }
}
