package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.stereotype.Service;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;

@Service
public interface RatingService {
    void setRating(Rating rating) throws RatingException;
    int getAverageRating(String game) throws RatingException;
    int getRating(String game, String player) throws RatingException;
    void reset() throws RatingException;
    void addRating(Rating rating) throws RatingException;
}
