package sk.tuke.kpi.kp.slitherlink.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import sk.tuke.kpi.kp.slitherlink.Service.ScoreService;

import java.util.List;
@RestController
@RequestMapping("/api/score")
public class ScoreServiceRest {

    @Autowired
    private ScoreService scoreService;

    @PostMapping
    public void addScore(@RequestBody Score score) {
        scoreService.addScore(score);
    }

    @GetMapping
    public List<Score> getAllScores() {
        return scoreService.getAllScores();
    }
}
