package sk.tuke.kpi.kp.slitherlink.server.controller;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/start")
public class MenuController {

    @GetMapping("/start")
    public String showMenu() {
        return "index";
    }

    @GetMapping("/game")
    public String startGame() {
        return "game";
    }

    @GetMapping("/comments")
    public String showComments() {
        return "comments";
    }

    @GetMapping("/rating")
    public String showRating() {
        return "rating";
    }
}
