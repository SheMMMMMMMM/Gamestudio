package sk.tuke.kpi.kp.slitherlink.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("Logging out user from /home. Session ID before invalidation: " + session.getId());
        try {
            session.invalidate();
            System.out.println("Session invalidated successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Error: Session already invalidated or not active: " + e.getMessage());
        }
        return "redirect:/login";
    }
}