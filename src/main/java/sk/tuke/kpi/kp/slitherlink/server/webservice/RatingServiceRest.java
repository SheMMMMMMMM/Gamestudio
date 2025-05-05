package sk.tuke.kpi.kp.slitherlink.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;
import sk.tuke.kpi.kp.slitherlink.Service.RatingException;
import sk.tuke.kpi.kp.slitherlink.Service.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingServiceRest {
    @Autowired
    private RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> addRating(@RequestBody Rating rating) {
        try {
            ratingService.addRating(rating);
            return ResponseEntity.ok("Оцінку додано.");
        } catch (RatingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Помилка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Помилка сервера: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Integer> getAverageRating(@RequestParam String game) {
        try {
            int averageRating = ratingService.getAverageRating(game);
            return ResponseEntity.ok(averageRating);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }
    @GetMapping("/{game}/{player}")
    public ResponseEntity<Integer> getRating(@PathVariable String game, @PathVariable String player) {
        try {
            int rating = ratingService.getRating(game, player);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }
}