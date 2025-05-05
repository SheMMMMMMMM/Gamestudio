package sk.tuke.kpi.kp.slitherlink.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sk.tuke.kpi.kp.slitherlink.Service.UserService;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        return "auth";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String player,
                               @RequestParam String password,
                               Model model) {
        if (userService.userExists(player)) {
            model.addAttribute("registerError", "Користувач з таким іменем вже існує!");
            return "auth";
        }

        userService.registerUser(player, password);
        return "redirect:/login";
    }
}