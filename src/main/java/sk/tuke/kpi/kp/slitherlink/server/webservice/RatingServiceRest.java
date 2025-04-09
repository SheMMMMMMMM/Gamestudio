package sk.tuke.kpi.kp.slitherlink.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;
import sk.tuke.kpi.kp.slitherlink.Service.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingServiceRest {
    @Autowired
    private RatingService ratingService;

    @GetMapping
    public int getAverageRating(@RequestParam String game) {
        return ratingService.getAverageRating(game);
    }
    @PostMapping
    public void addRating(@RequestBody Rating rating) {
        ratingService.addRating(rating);
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) {
        return ratingService.getRating(game, player);
    }
}
