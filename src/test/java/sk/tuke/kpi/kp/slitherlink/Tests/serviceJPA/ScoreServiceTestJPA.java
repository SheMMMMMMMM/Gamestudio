package sk.tuke.kpi.kp.slitherlink.Tests.serviceJPA;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import sk.tuke.kpi.kp.slitherlink.Service.ConcreteScoreService;
import sk.tuke.kpi.kp.slitherlink.Service.ScoreException;
import sk.tuke.kpi.kp.slitherlink.Service.ScoreService;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ScoreServiceTestJPA {

    @Test
    public void testAddScore() throws ScoreException {
        ScoreService scoreService = new ConcreteScoreService();

        Score score = new Score();
        score.setIdent(1);
        score.setPlayedOn(new Date());

        scoreService.addScore(score);

        assertNotNull(score);
    }

    @Test
    public void testGetAllScores() {
        ScoreService scoreService = new ConcreteScoreService();

        List<Score> scores = scoreService.getAllScores();

        assertTrue(scores.isEmpty());
    }

    @Test
    public void testReset() throws ScoreException {
        ScoreService scoreService = new ConcreteScoreService();

        scoreService.reset();

        assertDoesNotThrow(() -> scoreService.reset());
    }
}


