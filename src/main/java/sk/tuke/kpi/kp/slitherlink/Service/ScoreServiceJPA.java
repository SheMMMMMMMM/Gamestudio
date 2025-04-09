package sk.tuke.kpi.kp.slitherlink.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;

import java.util.Date;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public void addScore(Score score) throws ScoreException {
        try {
            if (score.getIdent() == 0) {
                score.setPlayedOn(new Date());
                entityManager.persist(score);
            } else {
                entityManager.lock(score, LockModeType.PESSIMISTIC_WRITE);
                entityManager.merge(score);
            }
        } catch (Exception e) {
            throw new ScoreException("Error saving score", e);
        }
    }
    @Override
    public List<Score> getAllScores() {
        try {
            return entityManager.createQuery("SELECT s FROM Score s", Score.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ScoreException("Error fetching all scores", e);
        }
    }

    @Override
    public List<Score> getTopScores(String game) throws ScoreException {
        try {
            return entityManager.createNamedQuery("Score.getTopScores", Score.class)
                    .setParameter("game", game)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new ScoreException("Error fetching scores");
        }
    }

    @Override
    public void reset() throws ScoreException {
        try {
            entityManager.createNamedQuery("Score.resetScores").executeUpdate();
        } catch (Exception e) {
            throw new ScoreException("Error resetting scores");
        }
    }
}
