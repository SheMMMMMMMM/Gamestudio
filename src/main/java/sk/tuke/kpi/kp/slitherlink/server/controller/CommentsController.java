package sk.tuke.kpi.kp.slitherlink.server.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommentsController {
    @GetMapping("/comments")
    public String showComments() {
        return "comments";
    }
}