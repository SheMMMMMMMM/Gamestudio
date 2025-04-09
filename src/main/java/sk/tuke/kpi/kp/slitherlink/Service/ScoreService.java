package sk.tuke.kpi.kp.slitherlink.Service;

import sk.tuke.kpi.kp.slitherlink.Entity.Score;

import java.util.List;

public interface ScoreService {
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores(String game) throws ScoreException;
    void reset() throws ScoreException;
    List<Score> getAllScores();  // Новий метод для отримання всіх записів
}
