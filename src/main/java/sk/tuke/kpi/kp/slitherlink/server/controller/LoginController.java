package sk.tuke.kpi.kp.slitherlink.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sk.tuke.kpi.kp.slitherlink.Service.UserService;


@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String loginPage() {
        return "auth";
    }

    @PostMapping
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        if (userService.authenticate(username, password)) {
            session.setAttribute("playerName", username);
            return "redirect:/home";
        } else {
            model.addAttribute("loginError", "Невірне ім'я користувача або пароль");
            return "auth";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("Logging out user from /game. Session ID before invalidation: " + session.getId());
        try {
            session.invalidate(); // Інвалідуємо сесію
            System.out.println("Session invalidated successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Error: Session already invalidated or not active: " + e.getMessage());
        }
        return "redirect:/home"; // Перенаправлення на /home для /game
    }
}