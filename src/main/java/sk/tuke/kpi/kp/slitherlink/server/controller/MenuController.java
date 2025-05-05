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

    // Відображення початкової сторінки
    @GetMapping("/start")
    public String showMenu() {
        return "index"; // Повертає HTML шаблон для початкового меню
    }

    // Переходи на сторінки гри, коментарів та рейтингів
    @GetMapping("/game")
    public String startGame() {
        return "game"; // Повертає сторінку гри
    }

    @GetMapping("/comments")
    public String showComments() {
        return "comments"; // Повертає сторінку коментарів
    }

    @GetMapping("/rating")
    public String showRating() {
        return "rating"; // Повертає сторінку рейтингу
    }
}
