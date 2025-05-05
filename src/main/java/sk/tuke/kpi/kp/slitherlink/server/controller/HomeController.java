package sk.tuke.kpi.kp.slitherlink.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String playerName = (String) session.getAttribute("playerName");
        if (playerName == null) {
            model.addAttribute("playerName", "Guest"); // Значення за замовчуванням, якщо сесія інвалідована
        } else {
            model.addAttribute("playerName", playerName);
        }
        return "home";
    }
}