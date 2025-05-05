package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;

@Service
public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/ratings";
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            restTemplate.postForEntity(url, rating, Rating.class);
        } catch (Exception e) {
            throw new RatingException("Error setting rating", e);
        }
    }
    @Override
    public int getAverageRating(String game) {
        return restTemplate.getForEntity(url + "/" + game, Integer.class).getBody();
    }

    @Override
    public int getRating(String game, String player) {
        return restTemplate.getForEntity(url + "/" + game + "/" + player, Integer.class).getBody();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}

