package sk.tuke.kpi.kp.slitherlink.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;

@Transactional
public class RatingServiceJPA implements RatingService {
    @Override
    public void addRating(Rating rating) throws RatingException {
        try {
            entityManager.persist(rating);
        } catch (Exception e) {
            throw new RatingException("Error saving rating", e);
        }
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.persist(rating);
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        return (int) entityManager.createNamedQuery("Rating.getAverageRating")
                .setParameter("game", game)
                .getSingleResult();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        return (int) entityManager.createNamedQuery("Rating.getUserRating")
                .setParameter("game", game)
                .setParameter("player", player)
                .getSingleResult();
    }

    @Override
    public void reset() {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}

