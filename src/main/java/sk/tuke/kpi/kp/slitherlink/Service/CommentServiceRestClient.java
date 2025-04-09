package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;
import java.util.Arrays;
import java.util.List;

@Service
public class CommentServiceRestClient implements CommentService {
    private final String url = "http://localhost:8080/api/comments";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addComment(Comment comment) {
        restTemplate.postForEntity(url, comment, Comment.class);
    }

    @Override
    public List<Comment> getComments(String game) {
        return Arrays.asList(restTemplate.getForEntity(url + "/" + game, Comment[].class).getBody());
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}

