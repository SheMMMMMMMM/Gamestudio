package sk.tuke.kpi.kp.slitherlink.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;
import sk.tuke.kpi.kp.slitherlink.Service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentServiceRest {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public void addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
    }

    @GetMapping("/{game}")
    public List<Comment> getComments(@PathVariable String game) {
        return commentService.getComments(game);
    }
}

