package sk.tuke.kpi.kp.slitherlink.Service;

import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import java.util.ArrayList;
import java.util.List;

public class ConcreteScoreService implements ScoreService {
    @Override
    public void addScore(Score score) throws ScoreException {
        if (score == null) {
            throw new ScoreException("Score cannot be null");
        }
    }

    @Override
    public List<Score> getAllScores() {
        return new ArrayList<>();
    }

    @Override
    public List<Score> getTopScores(String game) throws ScoreException {
        return new ArrayList<>();
    }
    @Override
    public void reset() throws ScoreException {
    }
}