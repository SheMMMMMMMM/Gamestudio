package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.stereotype.Service;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;

import java.util.List;

@Service
public interface CommentService {
    void addComment(Comment comment) throws CommentException;
    List<Comment> getComments(String game) throws CommentException;
    void reset() throws CommentException;
}
