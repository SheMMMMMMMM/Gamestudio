package sk.tuke.kpi.kp.slitherlink.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;
import sk.tuke.kpi.kp.slitherlink.Service.ScoreService;

import java.util.List;

@Controller
public class LeaderboardController {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        try {
            List<Score> topScores = scoreService.getTopScores("SlitherLink");
            logger.info("Top scores retrieved: {}", topScores); // Логування
            model.addAttribute("topScores", topScores);
        } catch (Exception e) {
            logger.error("Error retrieving top scores: {}", e.getMessage());
            model.addAttribute("error", "Не вдалося завантажити топ гравців.");
        }
        return "home";
    }
}