package sk.tuke.kpi.kp.slitherlink.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;

import java.util.Date;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addRating(Rating rating) throws RatingException {
        try {
            if (rating.getGame() == null || rating.getGame().isEmpty()) {
                throw new RatingException("Назва гри обов’язкова");
            }
            if (rating.getPlayer() == null || rating.getPlayer().isEmpty()) {
                throw new RatingException("Ім’я гравця обов’язкове");
            }
            if (rating.getRating() < 1 || rating.getRating() > 5) {
                throw new RatingException("Рейтинг має бути від 1 до 5");
            }
            if (rating.getRatedOn() == null) {
                rating.setRatedOn(new Date());
            }
            entityManager.persist(rating);
            entityManager.flush();
        } catch (Exception e) {
            throw new RatingException("Error saving rating", e);
        }
    }

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            entityManager.merge(rating);
        } catch (Exception e) {
            throw new RatingException("Error updating rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            Double average = (Double) entityManager.createNamedQuery("Rating.getAverageRating")
                    .setParameter("game", game)
                    .getSingleResult();
            return average != null ? average.intValue() : 0;
        } catch (Exception e) {
            return 0; // Якщо рейтингів немає, повертаємо 0
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            Integer rating = (Integer) entityManager.createNamedQuery("Rating.getUserRating")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
            return rating != null ? rating : 0;
        } catch (Exception e) {
            return 0; // Якщо рейтингу немає, повертаємо 0
        }
    }

    @Override
    public void reset() {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}